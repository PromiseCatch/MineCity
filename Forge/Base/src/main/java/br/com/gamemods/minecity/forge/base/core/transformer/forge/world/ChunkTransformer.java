package br.com.gamemods.minecity.forge.base.core.transformer.forge.world;

import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.base.core.transformer.InsertSetterGetterTransformer;

/**
 * Makes {@link net.minecraft.world.chunk.Chunk} implements {@link br.com.gamemods.minecity.forge.base.accessors.world.IChunk}
 * <pre><code>
 *     public class Chunk
 *         implements IChunk // <- Added
 *     {
 *         // ... original fields and methods
 *         public ClaimedChunk mineCity;
 *         public ClaimedChunk getMineCityClaim(){ return this.mineCity; }
 *         public void setMineCityClaim(ClaimedChunk claim){ this.mineCity = claim; }
 *     }
 * </code></pre>
 */
@Referenced
public class ChunkTransformer extends InsertSetterGetterTransformer
{
    public ChunkTransformer(String interfaceClass)
    {
        super(
                "net.minecraft.world.chunk.Chunk",
                "br.com.gamemods.minecity.structure.ClaimedChunk", "mineCity",
                interfaceClass, "setMineCityClaim", "getMineCityClaim"
        );
    }

    @Referenced("br.com.gamemods.minecity.forge.mc_1_10_2.core.MineCityFrostCoreMod")
    @Referenced("br.com.gamemods.minecity.forge.mc_1_12_2.core.MineCityColorCoreMod")
    public ChunkTransformer()
    {
        this("br.com.gamemods.minecity.forge.base.accessors.world.IChunk");
    }
}
