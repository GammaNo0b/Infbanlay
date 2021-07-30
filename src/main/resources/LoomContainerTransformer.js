function transform(methodNode) {
    var list = methodNode.instructions;

    for (var i = 0; i < list.size(); i++) {
        var insn = list.get(i);
        // search for 'bipush'-instructions that pushes the value 6 onto the stack
        if (insn.getOpcode() == Opcodes.BIPUSH && insn.operand == 6) {
            //	replace			bipush		6
            //	with			invokestatic	# ?			// Method me/gamma/infbanlay/Infbanlay.getMaxBannerLayers:()I;
            list.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, "me/gamma/infbanlay/Infbanlay", "getMaxBannerLayers", "()I"));
            break;
        }
    }

    return methodNode;
}

function transformMethod(classNode, desc, i) {
    var n = 0;
    // iterate over all methods in the class
    for (var j = 0; j < classNode.methods.size(); j++) {
        var methodNode = classNode.methods.get(j);
        // check if the descriptors are equal
        if (methodNode.desc === desc) {
            // check if the method is the nth method
            if (++n == i) {
                // transform method
                transform(methodNode);
                break;
            }
        }
    }
}

function createTransformer(desc, i) {
    return function(classNode) {
        transformMethod(classNode, desc, i);
        return classNode;
    };
}

function initializeCoreMod() {
    Opcodes = Java.type("org.objectweb.asm.Opcodes");
    MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    return {
        "LoomContainer.onCraftMatrixChanged": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.inventory.container.LoomContainer"
            },
            "transformer": createTransformer("(Lnet/minecraft/inventory/IInventory;)V", 1)
        },
        "LoomScreen.containerChange": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.client.gui.screen.LoomScreen"
            },
            "transformer": createTransformer("()V", 1)
        },
        "BannerItem.appendHoverTextFromTileEntityTag": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.item.BannerItem"
            },
            "transformer": createTransformer("(Lnet/minecraft/item/ItemStack;Ljava/util/List;)V", 1)
        }
    }
}