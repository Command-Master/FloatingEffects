package commandmaster.floateffects;

public interface FloatPacket { // stupid hack to allow casting to a mixin
    public boolean getIsFloat();
    public double getValue();
}
