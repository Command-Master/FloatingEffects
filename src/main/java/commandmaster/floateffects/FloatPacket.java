package commandmaster.floateffects.mixin;

public interface FloatPacket { // stupid hack to allow casting to a mixin
    public boolean getIsFloat();
    public double getValue();
}
