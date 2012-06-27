
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public final class TerrainMap
{
    /// Constant square root of two.
    private static final double SQRT_2 = Math.sqrt(2.0);

    /// Enumeration of possible Movement Types
    public enum MovementType
    {
        Chess, Euclidean, Manhattan
    }
    
    private final MovementType moveType;

    /// The world, represented as an array of values from 0 - 255 (inclusive)
    private final byte[][] Board;
    
    /// An array keeping track of which tiles have been visited.
    private final int[][] Uncovered;
    
    /// After computing a path from the start to the end, that path is stored here.
    private ArrayList<Point> path;
    
    /// Width of the world.
    private final int Width;
    
    /// Height of the world.
    private final int Height;
    
    /// Start point of your journey.
    private final Point StartPoint;
    
    /// End point of your journey.
    private final Point EndPoint;

    /// Timestamp of the last uncovered point.
    private int uncoveredCounter;

    /// Determines whether or not the map changes over time.
    private final boolean chaotic;

    /// Blocks functions during map chaotisism.
    private final Semaphore sem = new Semaphore(1, true);

    /// Activates map chaotisism at scheduled times.
    private final Timer timer = new Timer();

    /// Task for timer to activate.
    private final TimerTask task = new TimerTask()
    {
        @Override
        public void run()
        {
            runChaos();
        }
    };

    /// Creates a new map and forces the random number seed.
    public TerrainMap(final int width, final int height, final TerrainGenerator terraGen, final MovementType moveType, final boolean chaotic)
    {
        this.Width = width;
        this.Height = height;
        this.moveType = moveType;

        // State at the center and move towards a corner
        StartPoint = new Point((int) (0.5 * width), (int) (0.5 * height));
        EndPoint = new Point((int) (0.9 * width), (int) (0.9 * height));

        // Use one octave of Perlin noise to generate a wavy terrain.
        Board = terraGen.getTerrain();
        
        // Initially, nothing has been visited.
        Uncovered = new int[width][height];
        for(int x = 0; x < width; x++)
        {
            Arrays.fill(Uncovered[x], 0);
        }
        uncoveredCounter = 0;

        this.chaotic = chaotic;
    }

    /// Returns the start point of the path.
    public Point getStartPoint()
    {
        return new Point(StartPoint);
    }

    /// Returns the end point of the path.
    public Point getEndPoint()
    {
        return new Point(EndPoint);
    }

    /// Checks whether a tile is in the map.
    public boolean validTile(final int x, final int y)
    {
        return x >= 0 && x < Width && y >= 0 && y < Height;
    }

    /// Checks whether a tile is in the map.
    public boolean validTile(final Point pt)
    {
        return validTile(pt.x, pt.y);
    }

    /// Gets the value of a tile.
    public int getTile(final int x, final int y)
    {
        if(!validTile(x, y))
        {
            throw new IndexOutOfBoundsException("Tried to access (" + x + ", " + y + ") " +
                                            "in a board of dimension " + Width + " x " + Height);
        }
        down();
        if(Uncovered[x][y] == 0)
        {
            uncoveredCounter++;
            Uncovered[x][y] = uncoveredCounter;
        }
        up();
        return Board[x][y] & 0xFF;
    }

    /// Gets the value of a tile.
    public int getTile(final Point pt)
    {
        return getTile(pt.x, pt.y);
    }

    /// Determines if points are adjacent.
    public boolean isAdjacent(final Point p1, final Point p2)
    {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        if((moveType == MovementType.Manhattan) && (dx * dy != 0))
            return false;
        else
            return dx <= 1 && dy <= 1 && (dx != 0 || dy != 0);
    }

    /// Determines if two points are one diagonal away from each other.
    /**
     * Given two points, returns whether or not those points are diagonal.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @return Whether the points are diagonal.
     */
    public boolean isDiagonal(final Point p1, final Point p2)
    {
        final int dx = Math.abs(p1.x - p2.x);
        final int dy = Math.abs(p1.y - p2.y);
        if(dx * dy != 0)
            return false;
        else
            return dx <= 1 && dy <= 1 && (dx != 0 || dy != 0);
    }

    /// Gets an array of legal transitions.
    /**
     * Given a point, returns a list of all neighboring positions.
     *
     * @param pt The active point.
     * @return A list of all neighboring positions.
     */
    public Point[] getNeighbors(final Point pt)
    {
        final ArrayList<Point> neighbors = new ArrayList<Point>();
        for(int dx = -1; dx <= 1; dx++)
        {
            for(int dy = -1; dy <= 1; dy++)
            {
                if(dx == 0 && dy == 0)
                    continue;
                final Point temp = new Point(pt.x + dx, pt.y + dy);
                if(validTile(temp) && isAdjacent(pt, temp))
                    neighbors.add(temp);
            }
        }
        return neighbors.toArray(new Point[0]);
    }

    /// Returns the cost to move from one point to another.
    /**
     * The cost to move from one tile to another is exp(|h1 - h0|), where exp
     * is the exponential function and h1 and h0 are the heights of the tiles.
     * This function will mark both of the points as visited.
     * 
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The cost to move from the first point to the second point.
     */
    public double getCost(final Point p1, final Point p2)
    {
          //return (getTile(p2) / (getTile(p1)+1));
	     return Math.exp(getTile(p2) - getTile(p1));
    }
    
    /// Returns the width of the map.
    public int getWidth()
    {
        return Width;
    }
    
    /// Returns the height of the map.
    public int getHeight()
    {
        return Height;
    }
    
    /// Entry point to pathfinding.
    /**
     * Given an AI module, has the module compute the path from the start location to
     * the end location, then stores the path for later use.  The function also returns
     * the cost of the generated path.
     * 
     * @param module The AI module to use to compute the path.
     * @return The cost of the path.
     */
    public double findPath(final AIModule module)
    {
        if(chaotic)
            timer.scheduleAtFixedRate(task, 1000, 1000);
        final List<Point> ai_path = module.createPath(this);
        timer.cancel();
        return verifyPath(ai_path);
    }

    /// Confirms that a given path is legal and returns its cost
    /**
     * Given a path generated by an AI module, confirms that the path correctly
     * navigates from the start point to the end point.  Returns the cost of that
     * path.
     * 
     * @param path The generated path.
     * @return The cost to take the path.
     */
    private double verifyPath(final List<Point> path)
    {
        // First, make sure that we haven't already checked a path.
        if(this.path != null)
            throw new IllegalStateException("Attempted to register a path after a path has already been registered.");

        // Next, confirm that the path is non-null and actually contains points.
        if(path == null || path.isEmpty())
        {
            throw new RuntimeException("Empty Path");
        }

        // Make sure that we start and end at the correct points.
        if(!path.get(0).equals(StartPoint) || !path.get(path.size() - 1).equals(EndPoint))
        {
            throw new RuntimeException("Invalid Path");
        }

        // Confirm that each step in the path moves between adjacent points.
        for(int index = 0; index < path.size() - 1; index++)
        {
            if(!isAdjacent(path.get(index), path.get(index + 1)))
            {
                throw new RuntimeException("Invalid Path");
            }
        }

        double PathCost = 0;

        for(int index = 0; index < path.size() - 1; index++)
        {
            PathCost += getCost(path.get(index), path.get(index + 1));
        }
        
        // Deep-copy the path in case we need to modify it.
        this.path = new ArrayList<Point>(path);

        return PathCost;
    }

    /// Returns the number of squares that have been visited so far.
    public int getNumVisited()
    {
        int numVisited = 0;

        for(int i = 0; i < Width; i++)
        {
            for(int j = 0; j < Height; j++)
            {
                if(Uncovered[i][j] != 0)
                {
                    numVisited++;
                }
            }
        }
        return numVisited;
    }

    /// Returns a graphical representation of the generated path.
    /**
     * After calling findPath, you may call this function to retrieve a graphical
     * version of the map.
     * 
     * @return A graphical representation of the map.
     * @throws IllegalStateException If findPath hasn't completed successfully.
     */
    public BufferedImage createImage()
    {
        // Confirm that the path is valid.
        if(path == null)
        {
            throw new IllegalStateException("Attempted to create map image, but path isn't set.");
        }
        
        // Create a new image to hold the map.
        final BufferedImage im = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
        final WritableRaster raster = im.getRaster();
        final int[] pixels = new int[Width * Height * 3];
        for(int x = 0; x < Width; x++)
        {
            for(int y = 0; y < Height; y++)
            {
                final int offset = (y * Width + x) * 3;
                final int val = Board[x][y] & 0xFF;
                
                if(Uncovered[x][y] != 0)
                {
                    pixels[offset] = 0xFF;
                }
                else
                {
                    pixels[offset] = val;
                }
                
                pixels[offset + 1] = pixels[offset + 2] = val;
            }
        }
        
        raster.setPixels(0, 0, Width, Height, pixels);

        // Next, render the path in blue.
        for(final Point pt : path)
        {
            im.setRGB(pt.x, pt.y, Color.BLUE.getRGB());
        }
        
        // Finally, make the endpoints green.
        im.setRGB(StartPoint.x, StartPoint.y, Color.GREEN.getRGB());
        im.setRGB(EndPoint.x, EndPoint.y, Color.GREEN.getRGB());

        return im;
    }

    /// Returns a graphical representation of the contour map.
    /**
     * After calling findPath, you may call this function to retrieve a graphical
     * version of the contour map.
     *
     * @return A graphical representation of the map.
     * @throws IllegalStateException If findPath hasn't completed successfully.
     */
    public BufferedImage createContourImage()
    {
        // Confirm that the path is valid.
        if(path == null)
        {
            throw new IllegalStateException("Attempted to create map image, but path isn't set.");
        }

        // Create a new image to hold the map.
        final BufferedImage im = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
        final WritableRaster raster = im.getRaster();
        final int[] pixels = new int[Width * Height * 3];
        for(int x = 0; x < Width; x++)
        {
            for(int y = 0; y < Height; y++)
            {
                final int offset = (y * Width + x) * 3;
                pixels[offset] = (int)(0.5 + 0xFF * Uncovered[x][y] * 1.0 / uncoveredCounter);
                pixels[offset + 1] = pixels[offset + 2] = 0;
            }
        }
        raster.setPixels(0, 0, Width, Height, pixels);
        return im;
    }

    /// Creates a gray-scale BufferedImage representing the map.
    private BufferedImage toBufferedImage()
    {
        final BufferedImage im = new BufferedImage(Width, Height, BufferedImage.TYPE_BYTE_GRAY);
        final WritableRaster raster = im.getRaster();
        final int[] pixels = new int[Width * Height];
        for(int x = 0; x < Width; x++)
        {
            for(int y = 0; y < Height; y++)
            {
                pixels[y * Width + x] = Board[x][y];
            }
        }
        raster.setPixels(0, 0, Width, Height, pixels);
        return im;
    }

    /// Sets the terrain values based off of a gray-scale BufferedImage.
    private void toBoard(final BufferedImage im)
    {
        if(im.getType() != BufferedImage.TYPE_BYTE_GRAY || im.getWidth() != Width || im.getHeight() != Height)
            throw new RuntimeException("Something very bad has happened");
        final WritableRaster raster = im.getRaster();
        final int[] pixels = raster.getPixels(0, 0, Width, Height, new int[Width * Height]);
        for(int x = 0; x < Width; x++)
        {
            for(int y = 0; y < Height; y++)
            {
                Board[x][y] = (byte)pixels[y * Width + x];
            }
        }
    }

    /// Modify the terrain a little bit.
    private void runChaos()
    {
        final float f = -0.001f;
        final Kernel kernel = new Kernel(3, 3,
        new float[] {
            f, f, f,
            f, 1.008f, f,
            f, f, f});
        down();
        toBoard(new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null).filter(toBufferedImage(), null));
        up();
    }

    /// Aquires the semaphore.
    private void down()
    {
        try
        {
            sem.acquire();
        }
        catch(InterruptedException ex)
        {
            throw new RuntimeException("down function was interupted");
        }
    }

    /// Release the semaphore.
    private void up()
    {
        sem.release();
    }
}
