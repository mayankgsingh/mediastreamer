# mediastreamer
MP3 Audio streamer for home network.<br />This application will scan your media directories for MP3 files. Application will create index of MP3 data by reading MP3 TAG info from files. Apache Lucene is used to create index.

Once index is built and ready, users can start searching songs. The song will be searched simultaneously on multiple fields i.e. Song Name, Artist, Album and filename. Matched items will be displayed in results with hyperlink.

Clicking song name in results section, will start audio streaming .
## Screenshots
### Landing Page
![Landing Page](https://raw.githubusercontent.com/mayankgsingh/mediastreamer/master/screenshots/LandingPage.jpg)
### Search
![Search](https://raw.githubusercontent.com/mayankgsingh/mediastreamer/master/screenshots/SearchAndEnter.jpg)
### Select Song To Play
![Select Song.png](https://raw.githubusercontent.com/mayankgsingh/mediastreamer/master/screenshots/SelectSongToPlay.jpg)

## How to...
### Pre-requisites
 - JDK 1.7 or above.
 - Apache Maven - click [here](https://maven.apache.org) for download / installation details 
 - [Apache Tomcat](http://tomcat.apache.org/) or any equivalent web server.
 - Optionally, [eclipse](http://www.eclipse.org) or any other code editor.
### Code Checkout
Download or checkout code using git.
### Code Configurations 
Edit `mediastreamer/src/main/resources/app.properties` file.
 - `media.source.dir`: Specify source media directory which holds MP3 files.
 - `media.index.dir`: Specify directory which application can use to store index files.
### Build
1. If you are using Eclipse, use built in tomcat plugin to deploy application.
2. If you are on command line, use `mvn package` command to create war file.
### Deployment
Copy WAR file to tomcat `webapps` folder and start the server.
### Usage
1. Open browser and launch `http://<ip address>:<port>/mediastreamer/`
2. For first time launch, click on Setting icon (*top-right corner*) and Build/Re-build index. This might take a while depending upon the directory size.
3. Once index is built, try searching for desired song.
4. Click on song to start audio streaming.