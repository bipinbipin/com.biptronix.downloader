dependancies:
===========
ffmpeg
quvi
lame

process:
===========
1) get info from youtube link

   $ quvi dump

   https://www.youtube.com/watch?v=mBmL7iZpCb0

2) extract
    url
    thumbnail_url
    page_title

3) download the video stream

    $ ffmpeg -i "url" -vcodec copy -acodec copy FILEOUT.mkv

4) convert video to mp3

    $ ffmpeg -i FILEOUT.mkv -q:a 0 -map a FILEOUT

5) download thumbnail image file

   $ curl thumbnail_url > OUT.jpg

6) attach the image to the mp3

    $ lame --ti OUT.jpg page_title.mp3
