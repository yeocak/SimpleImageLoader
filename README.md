# *SimpleImageLoader*

Via this kotlin library;
  - You can download a photo
  - You can save the download photo to database
  - You can load the photo from database
  - Make all of them same time
  
    **With only one line of code!**

---

*Load image to your imageview. The tool gets the picture from the database if it's there already. If it isn't, tool downloads it from the internet and saves it to the database.*
> (Your imageview id).loadImage( "Photo Link", (Your Context) )


<br/>    

*Or you can modify the loading image.*
> (Your imageview id).loadImage( "Photo Link", (Your Context), cornerRadius = 20f, maxLength = 1000, errorDrawable = (Your Drawable), placeHolderDrawable = (Your Drawable) )

  - cornerRadius parameter for smoothing edges. (Default = 0f)
  - maxLength parameter for set the maximum length of the image. It determines quality of the image. Aspect ratio is maintained. (Default = 1000)
  - errorDrawable parameter for the drawable that will appear if the image fails to load.
  - placeHolderDrawable parameter for the drawable that will appear when the image starts to load.
  
---

To implement this library to your project:
  Add this lines to your build.gradle file:
    
    allprojects {
		  repositories {
			  ...
			  maven { url 'https://jitpack.io' }
		  }
	  }
    
<br/>   

  Add this line to your build.gradle(app) file:

    dependencies {
      ...
      implementation 'com.github.yeocak:SimpleImageLoader:0.3.0'
    }
