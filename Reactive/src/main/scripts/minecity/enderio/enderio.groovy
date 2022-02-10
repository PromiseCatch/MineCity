package minecity.enderio

import crazypants.enderio.conduits.conduit.AbstractConduit
import crazypants.enderio.conduits.conduit.BlockConduitBundle
import minecity.forge.Wrench

blockType(['enderio:blockTravelAnchor', 'enderio:blockDialingDevice']) {
    setReactive modifiableBlock
}

itemType(AbstractConduit) {
    setReactive new Conduit()
}

blockType([BlockConduitBundle, 'enderio:conduit_bundle_opaque']) {
    setReactive new Conduit()
}

itemType('enderio:itemYetaWrench') {
    setReactive new Wrench()
}
