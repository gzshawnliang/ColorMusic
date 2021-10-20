<%--
  Created by IntelliJ IDEA.
  User: Shawn
  Date: 2021/10/18
  Time: 23:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1.0, user-scalable=0" />
    <title>Color Music</title>

    <link href="/styles/kendo.common.min.css" rel="stylesheet" type="text/css" />
    <link href="/styles/kendo.material.min.css" rel="stylesheet" type="text/css" />
    <link href="/styles/bootstrap-player.css" rel="stylesheet">

    <script src="/js/jquery.min.js"></script>
    <script src="/js/kendo.web.min.js"></script>

<%--    <script src="/js/bootstrap-player.js"></script>--%>
        <script src="/js/html-midi-player@1.4.0.js"></script>

<%--    <script src="https://cdn.jsdelivr.net/combine/npm/tone@14.7.58,npm/@magenta/music@1.22.1/es6/core.js,npm/focus-visible@5,npm/html-midi-player@1.4.0"></script>--%>


</head>
<body>
Upload the picture <span>${name}!</span>

<div>
    <input id="photo" name="photo"  type="file" />
    <div>You can only upload <strong>JPG</strong>, <strong>PNG</strong> files.File size from 1kb to 1MB</div>
</div>
<br>
<div>
    <img id="img1" style="max-width: 100%">
</div>
<br>
<%--<div>--%>
<%--    <audio controls style="width:100%" id="Audio1">--%>
<%--        <source src="http://localhost:8080/file/wav" type="audio/wav" />--%>
<%--&lt;%&ndash;        <source src="http://localhost:8080/file/wav" type="audio/ogg" />&ndash;%&gt;--%>
<%--&lt;%&ndash;        <source src="http://www.w3schools.com/html/horse.mp3" type="audio/mpeg" />&ndash;%&gt;--%>
<%--&lt;%&ndash;        <a href="http://www.w3schools.com/html/horse.mp3">horse</a>&ndash;%&gt;--%>
<%--    </audio>--%>
<%--</div>--%>

<div>
    <midi-player style="width:100%;" id="midiPlayer1"
                 src="https://cdn.jsdelivr.net/gh/cifkao/html-midi-player@2b12128/twinkle_twinkle.mid"
            >
    </midi-player>
<%--    sound-font visualizer="#staffVisualizer1"--%>

<%--    <midi-visualizer type="staff" id="staffVisualizer1"--%>
<%--                     src="https://cdn.jsdelivr.net/gh/cifkao/html-midi-player@2b12128/twinkle_twinkle.mid">--%>
<%--    </midi-visualizer>--%>
</div>

<script type="text/javascript">
    $(document).ready(function() {
        //$("#Audio1").hide();
        $("#midiPlayer1").hide();
        $("#photo").kendoUpload({
            validation: {
                allowedExtensions: [".jpg",".jpeg", ".png"],
                maxFileSize: 1024000,
                minFileSize: 1024
            },
            multiple: false,
            // files: "[]",
            async: {
                saveUrl: "/file/upload",
                removeUrl: "/file/remove",
                autoUpload: false
            },
            select: function(e) {
                var fileInfo = e.files[0];
                var wrapper = this.wrapper;
                // $("#Audio1").hide();
                // $("#Audio1").trigger("pause");
                setTimeout(function () {
                    addPreview(fileInfo, wrapper);
                });
            },
            remove: function(e) {
                // $("#Audio1").hide();
                // $("#Audio1").trigger("pause");

                $("#midiPlayer1").hide();
                //$("#midiPlayer1").trigger("pause");

                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },
            clear: function(e) {
                // $("#Audio1").hide();
                // $("#Audio1").trigger("pause");
                $("#midiPlayer1").hide();
                //$("#midiPlayer1").trigger("pause");
                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },

            error: function(e) {
                // $("#Audio1").hide();
                // $("#Audio1").trigger("pause");
                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },
            complete: function(e) {
                console.log("Complete");
            },

            progress: function(e) {
                // $("#Audio1").hide();
                // $("#Audio1").trigger("pause");
                //kendoConsole.log("Upload progress :: " + e.percentComplete + "% :: " + getFileInfo(e));
            },

            success: function(e) {
                if(e.operation==='upload') {
                    var fileInfo = e.files[0];
                    var wavFilename = fileInfo.name.split('.').slice(0, -1).join('.') + ".wav"
                    var midiFilename = fileInfo.name.split('.').slice(0, -1).join('.') + ".midi"
                    console.log("Success:" + wavFilename);
                    console.log("Success:" + midiFilename);
                    var fileUrl2 = "/file/wav?file=" + wavFilename;
                    var fileUrl3 = "/file/midi?file=" + midiFilename;

                    // $("#Audio1").attr("src", fileUrl2).trigger("play");
                    // $("#Audio1").show();
                    var player = $("#midiPlayer1");

                    $("#midiPlayer1").attr("src", fileUrl3);
                    //player.src = fileUrl3;
                    player.show();
                    //$("#staffVisualizer1").attr("src", fileUrl3);
                }
            },

        });
    });
    function addPreview(file, wrapper) {
        var raw = file.rawFile;
        var reader  = new FileReader();

        if (raw) {
            reader.onloadend = function () {
                var preview = $("<img class='image-preview'>").attr("src", this.result);
                $("#img1").attr("src", this.result);

                wrapper.find(".k-file[data-uid='" + file.uid + "'] .k-file-group-wrapper")
                    .replaceWith(preview);
            };

            reader.readAsDataURL(raw);
        }
    }
</script>

<style>
    .image-preview {
        position: relative;
        vertical-align: top;
        height: 64px;
    }
</style>

</body>
</html>
