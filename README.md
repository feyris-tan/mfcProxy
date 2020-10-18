# mfc_proxy project

The website https://myfigurecollection.net provides an excellent resource of information about anime merchandising.

However, MFC does not provide an API to access its information, which provides an obstacle to programs that want to use its information.
This project is a way to programmatically access the information on myfigurecollection.net 

I began writing this when I got tired of parsing their HTML manually each time. Also I wanted to become more familiar with JSoup.
I took some inspiration from jikan.moe and vgmdb.info, APIs for MyAnimeList and VGMdb.net

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you do not know about Quarkus yet, go check it out, it's really cool: https://quarkus.io/ .

# Disclaimer

Since this program parses the HTML returned by the MFC website, this will probably break if MFC decides to change their layout.

If you use this, I'd recommend you to emply **heavy** client side caching. If a lot of people were to use this, this would cause stress on MFC's servers, and I wouldn't want that.
