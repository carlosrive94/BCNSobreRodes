package jmcdw.bcnsobrerodes;


import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.URL;

public class WMSUtils extends UrlTileProvider{

    // Construct with tile size in pixels, normally 256, see parent class.
    public WMSUtils(int x, int y) {
        super(x, y);
    }

    @Override
    public URL getTileUrl(int x, int z, int zoom) {
        return null;
    }

    private static final double[] TILE_ORIGIN = {2.0525032594168, 41.4690211603867};
    //array indexes for that data
    private static final int ORIG_X = 0;
    private static final int ORIG_Y = 1; // "

    // Size of square world map in meters, using WebMerc projection.
    private static final double MAP_SIZE = 20037508.34789244 * 2;

    // array indexes for array to hold bounding boxes.
    protected static final int MINX = 0;
    protected static final int MAXX = 1;
    protected static final int MINY = 2;
    protected static final int MAXY = 3;

    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    protected double[] getBoundingBox(int x, int y, int zoom) {
        double tileSize = MAP_SIZE / Math.pow(2, zoom);
        double minx = TILE_ORIGIN[ORIG_X] + x * tileSize;
        double maxx = TILE_ORIGIN[ORIG_X] + (x+1) * tileSize;
        double miny = TILE_ORIGIN[ORIG_Y] - (y+1) * tileSize;
        double maxy = TILE_ORIGIN[ORIG_Y] - y * tileSize;

        double[] bbox = new double[4];
        bbox[MINX] = 2.0525032594168;
        bbox[MINY] = 41.3494505099487;
        bbox[MAXX] = 2.26456091786947;
        bbox[MAXY] = 41.4690211603867;

        return bbox;
    }
}
