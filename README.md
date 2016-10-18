dependancies:
===========
1) ffmpeg

2) quvi

3) lame

process:
===========
1) get info from youtube link

    $ quvi dump https://www.youtube.com/watch?v=mBmL7iZpCb0

2) extract
    url
    thumbnail_url
    page_title

    from JSON structure:

    {
      "host": "youtube",
      "page_title": "Wax - No. 30003 (B)",
      "page_url": "https://www.youtube.com/watch?v=mBmL7iZpCb0",
      "id": "mBmL7iZpCb0",
      "format_requested": "default",
      "thumbnail_url": "https://i.ytimg.com/vi/mBmL7iZpCb0/default.jpg",
      "duration": "422000",
      "link": [
        {
          "id": "1",
          "url": "https://r4---sn-vgqs7nes.googlevideo.com/videoplayback?expire=1476789962&ipbits=0&sparams=dur%2Cei%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cnh%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&upn=Wj-enbnvfSo&initcwndbps=1090000&lmt=1297914443014018&ip=107.4.212.77&nh=IgpwZjAxLm9yZDM1Kg42Ni4yMDguMjMzLjE0MQ&key=yt6&itag=43&mime=video%2Fwebm&ratebypass=yes&ei=arIFWLP4EMTXugLA8IywDQ&mv=m&mt=1476767876&ms=au&source=youtube&signature=5E55E9D1CEBF5A0E3B10CE30FEBEA9323A659111.6EA7BDCB37CA539A408D608074128CFC542C35ED&pl=17&mn=sn-vgqs7nes&mm=31&dur=0.000&requiressl=yes&id=o-AOS49f72mBUrMq8ZPGjQwW8H10zdRrlka4RaMuUc03Qx"
        }
      ]
    }

3) download the video stream

    $ ffmpeg -i "url" -vcodec copy -acodec copy FILEOUT.mkv

4) convert video to mp3

    $ ffmpeg -i FILEOUT.mkv -q:a 0 -map a FILEOUT

5) download thumbnail image file

    $ curl thumbnail_url > OUT.jpg

6) attach the image to the mp3

    $ lame --ti OUT.jpg page_title.mp3
