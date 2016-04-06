# ZoomScrollView for Android
Adds zooming and scrolling functionality to your view.
# Usage 
Add ZoomScrollView.java to your project.
The easiest implementation is to wrap you view inside ZoomScrollView like this:

        // Create a view
        ImageView image = new ImageView(this);
        ..set it up
        // Create ZoomScrollView
        ZoomScrollView zoomScrollView = new ZoomScrollView(this, motherLayout, false, false);
        // add your view to ZoomScrollView
        zoomScrollView.addView(image);
You might have noticed that zoomScrollView does not get manually added to the main layout. This is done internally.

Alternatively, you could have your view extend the ZoomScrollView class.
# Example
In "Sample Project" you will find a project that demonstrates use of the ZoomScrollView class.
# Author
Fedil A.
