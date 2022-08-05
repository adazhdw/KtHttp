# KtHttp
Network request library packaged with okhttp3

[![](https://www.jitpack.io/v/adazhdw/KtHttp.svg)](https://www.jitpack.io/#adazhdw/KtHttp)

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
  
Step 2. Add the dependency

dependencies {
	        implementation 'com.github.adazhdw.KtHttp:0.3.9'
}

or

dependencies {
	        implementation 'com.github.adazhdw.KtHttp:lasupre:0.3.9'
          implementation 'com.github.adazhdw.KtHttp:kthttp:0.3.9'
          implementation 'com.github.adazhdw.KtHttp:converter-jackson:0.3.9'
          implementation 'com.github.adazhdw.KtHttp:converter-gson:0.3.9'
          implementation 'com.github.adazhdw.KtHttp:adapter-rxjava3:0.3.9'
}
