<!DOCTYPE HTML>
<html>
  <head>
    <script src="script.js"></script>
    <link rel="stylesheet" href="style.css"/>
  </head>
  <body>
  <h1>This is a test index page <?php echo "rendered using php"; ?>!</h1>

    <p>As you can see, it is displayed properly!</p>

    <img src="image.jpg" width="50%" />
    <br>
    <i>Photo source: <a href="https://commons.wikimedia.org/wiki/File:Gyeonghoeru_(Royal_Banquet_Hall)_at_Gyeongbokgung_Palace,_Seoul.jpg#/media/File:Gyeonghoeru_(Royal_Banquet_Hall)_at_Gyeongbokgung_Palace,_Seoul.jpg">here</a></i>
    <br>

    <button onclick="test()">Even javascript files are sent properly</button>

    <p>You can also see from the colors that external css works as well. (And if you run the web server with the -s flag it will santify the page)</p>


    <form method="post" action="/test.php">
      <label>This form will demonstrate handling post requests with php!</label>
      <input type="text" name="inp" />
      <input type="submit" />
    </form>
  </body>
</html>
