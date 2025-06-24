package dev.akarah.qh.client.render;

import net.minecraft.util.ARGB;

public record RenderColor(int alpha, int red, int blue, int green) {
    public int argb() {
        return ARGB.color(this.alpha, this.red, this.blue, this.green);
    }

    public float alphaFloat() {
        return ((float) this.alpha) / 255.0f;
    }

    public float redFloat() {
        return ((float) this.red) / 255.0f;
    }

    public float blueFloat() {
        return ((float) this.blue) / 255.0f;
    }

    public float greenFloat() {
        return ((float) this.green) / 255.0f;
    }
}
