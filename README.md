# Seam Carver - Content-aware image resizing

An optimized and improved implementation of the seam-carving algorithm [(Shamir and Avidan, Mitsubishi Labs, 2007)](http://www.faculty.idc.ac.il/arik/SCWeb/imret/imret.pdf) to create faster renders and provide real-time previews.

<center><img src="docs\assets\seam-carver.gif" width="80%" align="center"></center><br>

>Red lines in the frames above represent the least energy pixel path

[See what makes this implementation so efficient](#optimizations-and-improvements-on-the-original-algorithm)

## Try out the GUI application

A basic GUI application (JavaFX + Java, packaged as an executable) that displays real-time, interactable renders from the algorithms implemented in this project. 

[Click here to download the executable application](http://faceresolve.com/wp-content/uploads/2019/08/Seam_Carver.zip)

Simply run the .exe file, click the 'Load image' button, and then move the slider around once the image is loaded. 

> Note: Render speed varies by processing power of the computer you're using. For real-time previews, it is recommended to use an image that is no larger than 1500w x 800h

## Sample images

<center><img src="docs\assets\samples.jpg" width="80%" align="center"></center><br>

## The Algorithm

### What is content-aware resizing/seam carving?

The seam carving algorithm dynamically assigns a weight to all the pixels in an image based on an 'energy' formula and then finds a vertical path of pixels with the least total weight. If the image is to be downscaled this path is removed, else if the image is to be upscaled the path is duplicated. This enables resizing of the image with the least amount of disruption to the contents of the image.

For a more detailed explanation of the original algorithm, please see [this document](http://www.faculty.idc.ac.il/arik/SCWeb/imret/imret.pdf).

### Energy function for this implementation

This implementation uses the improved forward-energy formula as [described in this paper](http://www.eng.tau.ac.il/~avidan/papers/vidret.pdf). The forward-energy critereon is known to produce better results over the original energy formula, especially whith images that contain horizontal lines:

// TODO example

### Using Dijkstra's shortest-path algorithm to find seams

This implementation leverages [Dijkstra's shortest-path algorithm](https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/) to calculate vertical paths of the least energy/weight through the image. Here's a brief explanation of how this is used in this implementation:

1. Assign energy/weight values to all pixels in the image using an energy critereon
2. Work top-down to create a matrix of cumulative energy values for each pixel in the image. As the Cumulative Energy matrix is populated, keep track of the paths followed in a Backtracking matrix.
3. Once the Cumulative Energy matrix and the Backtracking matrix are both populated, find the entry in the last row of the Cumulative Energy matrix with the least cumulative energy, and use the Backtracking matrix to find the path upwards

## Optimizations and improvements on the original algorithm

### Up to 50x faster with memoization and caching

This version achieves significantly faster renders by caching the seams in order of least to most energy. Here is the difference in steps between this and the original algorithm:

// TODO flow diagram

### Half the number of array traversals between 'crops' with precise pixel management

The original algorithm iterates through all the pixels in the image after each iteration of removing pixels. This is unneccesary since most of the information within the image remains unchanged, and only a part of the pixels in the image need to be 'updated'. Here are the optimizations between the original algorithm and this implementation, specifically regarding updating the positions and energies of pixels after each iteration of pixel removal.

// TODO diagrams

## Implementation-specific optimizations

### O(1) access/update of pixels in image with a smart EnergyMatrix class

When the image is imported, the pixel RGB data is stored in an [EnergyMatrix](src\model\EnergyMatrix.java) class with an underlying 2D [ArrayList<ArrayList<Pixel>> object](http://infotechgems.blogspot.com/2011/11/java-collections-performance-time.html) that has O(1) time complexity for adding/getting elements. This allows accessing and updating image data with O(1) time complexity. This is a powerful performance gain for this algorithm since it depends heavily on dynamically manipulating the pixel data in the image.

### O(1) removal/addition of pixel from image by 'disabling/enabling' pixels rather than removing/adding

While the use of 2D ArrayLists allows for adding/updating pixels form the image in constant/O(1) time, removing pixels would still be in linear time/O(n) since ArrayLists' `remove()` method [works in linear time](http://infotechgems.blogspot.com/2011/11/java-collections-performance-time.html). 

However, this implementation still manages to remove pixels in constant time/O(1) by 'disabling' pixels in the image rather than actually removing them from the ArrayList. 'Disabling' a pixel involves setting a property on the Pixel object (by calling `setActive()`) which is an O(1) operation. Then, when the 2D ArrayList is to be exported as an image, we check for disabled pixels (`isActive()`), which is also in constant time, and ignore them when producing the BufferedImage object. This way, we can 'remove' pixels from the image in constant time.

// TODO diagram

### Non-destructive cropping mechanism allows interactive resizing

Using the `setActive()` and `isActive()` methods of the Pixel object, we can non-desctruvtively 'remove' pixels from the 2D EnergyMatrix by disabling them. This has another advantage - it allows us to undo any pixel removals without reverse calculating.

If you try out the GUI application linked above, you will notice that if you drag the slider back and forth the pixels that were removed from the image are added back in exactly the same place (we undo the pixel removal) without data-loss. This is not easily possible with the original algorithm as pixels that are once removed cannot be 're-calculated' and 're-positioned' back in the right place. You would have to start again from the original image and crop the number of pixels that you want.

However, since this implementation never really removes the pixels from the 2D EnergyMatrix, adding them back in is simply a matter of reactivating them in the ArraList object.

// TODO Slider resizing

## Future improvements

### Leverage GPU computation for data-parallel tasks

## Contribute

Pull requests are more than welcome!

