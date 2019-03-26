public class IvRaytracableRotater {
    public IvRaytraceableAxisAlignedBox getInterpolatedRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, double x1, double y1, double z1, double width1, double height1, double depth1, float fraction)
    {
        double xI = IvMathHelper.mix(x, x1, fraction);
        double yI = IvMathHelper.mix(y, y1, fraction);
        double zI = IvMathHelper.mix(z, z1, fraction);

        double wI = IvMathHelper.mix(width, width1, fraction);
        double hI = IvMathHelper.mix(height, height1, fraction);
        double dI = IvMathHelper.mix(depth, depth1, fraction);

        return getRotatedBox(userInfo, xI, yI, zI, wI, hI, dI);
    }


    public IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth)
    {
        return getRotatedBox(userInfo, x, y, z, width, height, depth, getFacing(), new double[]{getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5});
    }

    public static IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords)
    {
        IvRaytraceableAxisAlignedBox box = null;

        switch (direction) {
            case SOUTH:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
                break;
            case WEST:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
                break;
            case NORTH:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
                break;
            case EAST:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
                break;
        }

        return box;
    }
}