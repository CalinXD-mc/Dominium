#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec3 Gray;
uniform vec3 RedMatrix;
uniform vec3 GreenMatrix;
uniform vec3 BlueMatrix;
uniform vec3 Offset;
uniform vec3 ColorScale;

uniform float Saturation;
uniform float Folly;
uniform float Darkness;

uniform float Time;
uniform vec2 Frequency;
uniform vec2 WobbleAmount;

out vec4 fragColor;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = c.g < c.b ? vec4(c.bg, K.wz) : vec4(c.gb, K.xy);
    vec4 q = c.r < p.x ? vec4(p.xyw, c.r) : vec4(c.r, p.yzx);
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    float xOffset = sin(texCoord.y * Frequency.x + Time * 6.283185307) * WobbleAmount.x;
    float yOffset = cos(texCoord.x * Frequency.y + Time * 6.283185307) * WobbleAmount.y;
    vec2 offset = texCoord + vec2(xOffset, yOffset);

    vec4 InTexel = texture(DiffuseSampler, offset);

    float RedValue   = dot(InTexel.rgb, RedMatrix);
    float GreenValue = dot(InTexel.rgb, GreenMatrix);
    float BlueValue  = dot(InTexel.rgb, BlueMatrix);
    vec3 OutColor    = vec3(RedValue, GreenValue, BlueValue);
    OutColor         = (OutColor * ColorScale) + Offset;

    vec3 hsv = rgb2hsv(InTexel.rgb);
    bool isCyanRange =
        hsv.x > 0.49 && hsv.x < 0.54 &&
        hsv.y > 0.5 && hsv.z > 0.5;

    if (!isCyanRange) {
        float Luma   = dot(OutColor, Gray);
        vec3 Chroma  = OutColor - Luma;
        OutColor     = (Chroma * Saturation) + Luma;
    }

    hsv = rgb2hsv(OutColor);
    if (!isCyanRange) {
        hsv.x = fract(hsv.x + Time * 0.05);
    }
    OutColor = hsv2rgb(hsv);

    float f = Folly * 0.5;
    OutColor.r += f;
    OutColor.b += f * 0.35;

    OutColor *= (1.0 - Darkness);

    fragColor = vec4(OutColor, 1.0);
}