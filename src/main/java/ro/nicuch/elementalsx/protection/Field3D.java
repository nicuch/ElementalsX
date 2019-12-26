package ro.nicuch.elementalsx.protection;

public class Field3D {
    private final int x;
    private final int y;
    private final int z;

    public Field3D(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final int getZ() {
        return this.z;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Field3D)) return false;
        Field3D field3D = (Field3D) o;
        return this.x == field3D.x &&
                this.y == field3D.y &&
                this.z == field3D.z;
    }

    @Override
    public int hashCode() {
        return this.x * 239 + this.y * 191 + this.z * 157;
    }
}
