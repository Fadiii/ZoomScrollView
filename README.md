# ZoomScrollView
Implements Zooming and Scrolling functionality
# Usage 
The easiest way to get this working is to wrap you view inside ZoomScrollView like this:
        // Create a view
        ImageView image = new ImageView(this);
        ..set it up
        // Create ZoomScroll view
        ZoomScrollView layout = new ZoomScrollView(this, motherLayout, false, false);
        // add your view to ZoomScroll
        layout.addView(image);
# Author
Fedil A.
