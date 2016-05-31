package org.jmc.gui;

/**
 * @author paul
 */
public class ContentUtil {
    public static final String[] CONTENT_FAQ = new String[]{

        "Where is the exported file ?",
        "For every export, KubiCraft bundles a zip file named as the used world save. Each export contains one .obj file, one .mtl file and a textures folder.\n\n" +
                "Each zip can easily be found in a folder called 'kubicraft-exports' next to the KubiCraft application.",

        "Why is there an error when my export is over 2 Million triangles ?",
        "When the exported model has over 2 Million triangles, Kubity format conversion has to remove mesh vertexes in order to optimize the playing " +
                "experience on the desktop and mobile applications. \n\n" +
                "This optimization approach is very wide scoped (optimize any 3d mesh) and unfortunately can introduce important artefacts in minecraft " +
                "exports. \n\n" +
                "For now, sticking to the 2 Million triangles mark is the best way of having an artefact free export.",

        "Can I use my own resource pack ?",
            "Yes you can ! And if some textures are missing, we replace them with defaults textures.\n" +
            "However, we only support texture packs with version 1.6 or over.",

        "Why my custom resource pack does not show exactly as in minecraft ? ",
            "That's because some resource packs require special mods such as Optifine or MCPatcher.\n\n" +
                    "We cannot use the enhanced textures for this pack. " +
                    "So we try our best to export the textures anyway. That's why you don't have connected textures, or shaders, yet...",

        "Why is my world save not working ?",
        "We currently only support minecraft anvil format.\n\n" +
                "We do not support MCRegion format. We do not support MCEdit Schematic format neither.",

        "Why some entities are missing ?",
        "We do not currently support all minecraft versions entities but we are working on adding as much as possible.",

        "Why some textures or blocks are pink ?",
        "We do not currently support all minecraft textures but we are working as much as possible.",
    };

    public static final String[] CONTENT_ABOUT = new String[]{

            "Why KubiCraft ?",
            "This project is based on the awesome jmc2obj exporter.\n\n" +
                    "However we really want to simplify the usage experience and auto-export to Kubity.\n\n" +
                    "The project is open-source so just go check the code at https://github.com/adriangonzy/KubiCraft",

            "But what is Kubity and why we should use it ?",
                    "We believe it's a great way to share your minecraft builds, awesome spawn points or your cute little cottage by the river ;)\n\n" +
                    "Anyone will be able to freely walk on the terrain you exported and feel like in the game directly from their browsers and mobile phones.\n\n" +
                    "For even more awesomeness, Kubity mobile Apps will let you rediscover your exports in Virtual Reality !!",

            "Next Steps",
            "We are working on bigger exports, better entities support, faster and easier export preview and even more...\n\n" +
                    "Stay updated for the goodness.",

            "Contact",
            "We are a group of 3 developpers passionate about Minecraft and all of it's possibilities.\n\n" +
                    "We wanted to share this side project exporter with the community.\n\n" +
                    "We would LOVE to get some feedback from you, so just email us at minecraft@kubity.com."
    };
}
