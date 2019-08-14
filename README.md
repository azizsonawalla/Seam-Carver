# Seam Carver - Content-aware image resizing

An optimized and improved implementation of the [seam-carving algorithm (Shamir and Avidan, Mitsubishi Labs, 2007)](http://www.faculty.idc.ac.il/arik/SCWeb/imret/imret.pdf) to create faster renders and provide real-time previews.

## Try out the GUI application

A basic GUI application (JavaFX + Java, packaged as an executable) that displays real-time, interactable renders from the algorithms implemented in this project. 

[Click here to download the executable application]() //TODO
Simply run the .exe file, click the 'Load image' button, and then move the slider around once the image is loaded. 

> Note: Render speed varies by processing power of the computer you're using. For real-time previews, it is recommended to use an image that is no larger than 1500w x 800h

## Sample images

// TODO

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
3. 

## Optimizations and improvements on the original algorithm

### Up to 50x faster with memoization and caching

This version achieves significantly faster renders by caching the seams in order of least to most energy. Here is the difference in steps between this and the original algorithm:



### Half the number of array traversals between 'crops' with precise pixel management

## Implementation-specific optimizations

### O(1) access/update of pixels in image with a smart EnergyMap class

### O(1) removal/addition of pixel with the use of 'activate' in custom Pixel class

## Future improvements

### Leverage GPU computation for data-parallel tasks

## Contribute

