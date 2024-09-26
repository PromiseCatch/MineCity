package br.com.gamemods.minecity.forge.base.core.transformer.forge.block;

import br.com.gamemods.minecity.api.CollectionUtil;
import br.com.gamemods.minecity.forge.base.core.MethodPatcher;
import br.com.gamemods.minecity.forge.base.core.ModEnv;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.base.core.transformer.BasicTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.Comparator;
import java.util.Random;

import static org.objectweb.asm.Opcodes.*;

/**
 * Patches all implementations to {@code {@link net.minecraft.block.IGrowable#grow(net.minecraft.world.World, Random, net.minecraft.util.math.BlockPos, net.minecraft.block.state.IBlockState)}}
 * to capture the changes and send them as a cancellable event.
 *
 * <p>In vanilla minecraft 1.10.2 with forge only BlockCocoa, BlockCrops, BlockDoublePlant, BlockGrass, BlockMushroom, BlockSapling, BlockStem and BlockTallGrass are patched
 *
 * <p>Example:
 * <code><pre>
 *     public class Something implements IGrowable
 *     {
 *         public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
 *         {
 *             // the growth code
 *         }
 *     }
 * </pre></code>
 *
 * <p>Will be transformed to:
 * <code><pre>
 *     public class Something implements IGrowable
 *     {
 *         public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
 *         {
 *             try
 *             {
 *                 MineCityHook.startCapturingBlocks(worldIn);
 *                 // the growth code
 *                 MineCityHook.onGrowableGrow(null, this, worldIn, pos, state);
 *             }
 *             catch(Throwable e)
 *             {
 *                 // The exception will be rethrown
 *                 MineCityHook.onGrowableGrow(e, this, worldIn, pos, state);
 *             }
 *         }
 *     }
 * </pre></code>
 */

@Referenced
@MethodPatcher
public class GrowMonitorTransformer extends BasicTransformer
{
    private String hookClass = ModEnv.hookClass.replace('.','/');

    @Referenced("br.com.gamemods.minecity.forge.mc_1_7_10.core.MineCitySevenCoreMod")
    @Referenced("br.com.gamemods.minecity.forge.mc_1_10_2.core.MineCityFrostCoreMod")
    @Referenced("br.com.gamemods.minecity.forge.mc_1_12_2.core.MineCityColorCoreMod")
    public GrowMonitorTransformer()
    {
        super(true);
        this.writerFlags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
    }

    public GrowMonitorTransformer(String accept)
    {
        super(accept);
        this.writerFlags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
    }

    @Override
    protected void patch(String srg, ClassNode node, ClassReader reader)
    {
        boolean patched = false;
        for(MethodNode method : node.methods)
        {
            // 1.10.2
            if(method.desc.equals("(Lnet/minecraft/world/World;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V")
                    && (method.name.equals("func_176474_b") || method.name.equals("grow")))
            {
                if(method.instructions.size() == 0)
                    break;
                patched = true;
                patchGrowFrost(srg, method);
                break;
            }

            // 1.7.10
            else if(method.desc.equals("(Lnet/minecraft/world/World;Ljava/util/Random;III)V") && method.name.equals("func_149853_b"))
            {
                if(method.instructions.size() == 0)
                    break;
                patched = true;
                patchGrowSeven(srg, method, 0, 1, 3, 4, 5);
                break;
            }
        }

        if(!patched)
            this.abort = true;
    }

    protected void patchGrowFrost(String srg, MethodNode method)
    {
        System.out.println("\n | - Inserting try-finally block to "+srg+"#"+method.name+method.desc);

        CollectionUtil.stream(method.instructions.iterator())
                .filter(ins-> ins.getOpcode() == RETURN)
                .map(ins-> method.instructions.indexOf(ins))
                .sorted(Comparator.reverseOrder()).mapToInt(Integer::intValue)
                .forEachOrdered(index -> {
                    InsnList list = new InsnList();
                    list = new InsnList();
                    list.add(new InsnNode(ACONST_NULL));
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new VarInsnNode(ALOAD, 1));
                    list.add(new VarInsnNode(ALOAD, 3));
                    list.add(new VarInsnNode(ALOAD, 4));
                    list.add(new MethodInsnNode(INVOKESTATIC,
                            hookClass, "onGrowableGrow",
                            "(Ljava/lang/Throwable;Ljava/lang/Object;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V",
                            false
                    ));
                    method.instructions.insertBefore(method.instructions.get(index), list);
                });

        InsnList list = new InsnList();
        LabelNode labelStart = new LabelNode();
        LabelNode labelEnd = new LabelNode();
        LabelNode labelCatch = new LabelNode();

        list.add(labelStart);
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC,
                hookClass, "startCapturingBlocks", "(Lnet/minecraft/world/World;)V", false
        ));
        method.instructions.insert(list);

        list = new InsnList();
        list.add(labelEnd);
        list.add(labelCatch);
        list.add(new VarInsnNode(ALOAD, 0));
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new VarInsnNode(ALOAD, 3));
        list.add(new VarInsnNode(ALOAD, 4));
        list.add(new MethodInsnNode(INVOKESTATIC,
                hookClass, "onGrowableGrow",
                "(Ljava/lang/Throwable;Ljava/lang/Object;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V",
                false
        ));
        list.add(new InsnNode(RETURN));

        method.instructions.add(list);
        method.tryCatchBlocks.add(new TryCatchBlockNode(labelStart, labelEnd, labelCatch, null));
    }

    /**
     * @param params this world x y z
     */
    protected void patchGrowSeven(String srg, MethodNode method, int... params)
    {
        System.out.println("\n | - Inserting try-finally block to "+srg+"#"+method.name+method.desc);

        CollectionUtil.stream(method.instructions.iterator())
                .filter(ins-> ins.getOpcode() == RETURN)
                .map(ins-> method.instructions.indexOf(ins))
                .sorted(Comparator.reverseOrder()).map(Integer::intValue)
                .forEachOrdered(index -> {
                    InsnList list = new InsnList();
                    list = new InsnList();
                    list.add(new InsnNode(ACONST_NULL));
                    list.add(new VarInsnNode(ALOAD, params[0]));
                    list.add(new VarInsnNode(ALOAD, params[1]));
                    list.add(new VarInsnNode(ILOAD, params[2]));
                    list.add(new VarInsnNode(ILOAD, params[3]));
                    list.add(new VarInsnNode(ILOAD, params[4]));
                    list.add(new MethodInsnNode(INVOKESTATIC,
                            hookClass, "onGrowableGrow",
                            "(Ljava/lang/Throwable;Ljava/lang/Object;Lnet/minecraft/world/World;III)V",
                            false
                    ));
                    method.instructions.insertBefore(method.instructions.get(index), list);
                });

        InsnList list = new InsnList();
        LabelNode labelStart = new LabelNode();
        LabelNode labelEnd = new LabelNode();
        LabelNode labelCatch = new LabelNode();

        list.add(labelStart);
        list.add(new VarInsnNode(ALOAD, 1));
        list.add(new MethodInsnNode(INVOKESTATIC,
                hookClass, "startCapturingBlocks", "(Lnet/minecraft/world/World;)V", false
        ));
        method.instructions.insert(list);

        list = new InsnList();
        list.add(labelEnd);
        list.add(labelCatch);
        list.add(new VarInsnNode(ALOAD, params[0]));
        list.add(new VarInsnNode(ALOAD, params[1]));
        list.add(new VarInsnNode(ILOAD, params[2]));
        list.add(new VarInsnNode(ILOAD, params[3]));
        list.add(new VarInsnNode(ILOAD, params[4]));
        list.add(new MethodInsnNode(INVOKESTATIC,
                hookClass, "onGrowableGrow",
                "(Ljava/lang/Throwable;Ljava/lang/Object;Lnet/minecraft/world/World;III)V",
                false
        ));
        list.add(new InsnNode(RETURN));

        method.instructions.add(list);
        method.tryCatchBlocks.add(new TryCatchBlockNode(labelStart, labelEnd, labelCatch, null));
    }
}
