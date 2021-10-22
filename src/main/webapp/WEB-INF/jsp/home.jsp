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

    <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link rel="manifest" href="/site.webmanifest">
    <link rel="mask-icon" href="/safari-pinned-tab.svg" color="#5bbad5">
    <meta name="msapplication-TileColor" content="#da532c">
    <meta name="theme-color" content="#ffffff">

    <title>Color Music</title>

    <link href="/styles/kendo.common.min.css" rel="stylesheet" type="text/css" />
    <link href="/styles/kendo.material.min.css" rel="stylesheet" type="text/css" />
<%--    <link href="/styles/bootstrap-player.css" rel="stylesheet">--%>

    <script src="/js/jquery.min.js"></script>
    <script src="/js/kendo.web.min.js"></script>

<%--    <script src="/js/bootstrap-player.js"></script>--%>
    <script src="/js/html-midi-player@1.4.0.js"></script>


</head>
<body>
<div>
    <p>Select the painting
        <input id="painting" value="1" style="width: 100%;" />
    </p>

    Or upload your painting <span>!</span>
    <div>
        <input id="photo" name="photo"  type="file" />
        <div>You can only upload <strong>JPG</strong>, <strong>PNG</strong> files.File size from 1KB to 1MB</div>
    </div>

    <br>
    <div style="margin-right: 8px">
        <midi-player style="width:100%;" id="midiPlayer1"
                     src="https://cdn.jsdelivr.net/gh/cifkao/html-midi-player@2b12128/twinkle_twinkle.mid"
        >
        </midi-player>
    </div>
    <br>
    <div style="text-align: center">
        <img id="img1" style="max-width: 100%">
    </div>




</div>


<%--<script id="template" type="text/x-kendo-template">--%>
<%--    <span class="k-state-default" style="background-image: url('/images/2c7211e2-c28e-4afa-8102-d72f99176c13.jpg')"></span>--%>
<%--    <span class="k-state-default" style="padding-left: 15px;"><h5>#:data.text#</h5></span>--%>
<%--</script>--%>


<script type="text/javascript">

    function _uuid() {
        var d = Date.now();
        if (typeof performance !== 'undefined' && typeof performance.now === 'function'){
            d += performance.now(); //use high-precision timer if available
        }
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
    }

    function clearMidiPlayer()
    {
        $("#midiPlayer1")[0].stop();
        $("#midiPlayer1").attr("src", '');
        $("#midiPlayer1").hide();
    }

    function showMidiPlayer(url)
    {
        $("#midiPlayer1").attr("src", url);
        $("#midiPlayer1").show();
    }

    function startMidiPlayer(url)
    {
        showMidiPlayer(url);

        setTimeout(function () {
            $("#midiPlayer1")[0].start();
        },500);

    }


    var newFileName='';

    //todo 增加png支持
    var data = [
        { text: "   ", value: "0" },
        { text: "Vassily Kandinsky,1923 - Composition 8, huile sur toile,Musée Guggenheim", value: "37025235-6ea3-4c16-a234-71c5b80f4a9e.jpg" },
        { text: "Vassily Kandinsky,1923 - On White II", value: "2c7211e2-c28e-4afa-8102-d72f99176c13.jpg" },
        { text: "Vassily Kandinsky,1923 - Circles in a Circle", value: "14c8817c-cfd4-406e-9c32-2ee6af5a76c5.jpg" },
        { text: "My self-portrait", value: "ce1816c8-858b-44b1-ac08-23030c7e9a10.jpg" },
        { text: "Just listen", value: "63b02862-e968-44b1-a71e-744dfff4048c.png" }
    ];

    $(document).ready(function() {


        $("#painting").kendoDropDownList({
            dataTextField: "text",
            dataValueField: "value",
            dataSource: data,
            // valueTemplate:kendo.template($("#template").html()),
            value: "0",
            change: function(e) {
                var value = this.value();
                console.log(value);
                if(value ==='0') {
                    $("#img1").attr("src", "");
                    clearMidiPlayer();
                }
                else
                {
                    $("#midiPlayer1")[0].stop();
                    $("#photo").data("kendoUpload").removeAllFiles();

                    setTimeout(function () {
                        $("#img1").attr("src", "/images/"+value);
                        var midiFilename = value.split('.').slice(0, -1).join('.') + ".midi"

                        showMidiPlayer("/images/"+midiFilename);
                    },100);
                }

            },
        });


        clearMidiPlayer();

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
                var dropdownlist = $("#painting").data("kendoDropDownList");
                dropdownlist.value("0");

                clearMidiPlayer();


                setTimeout(function () {
                    addPreview(fileInfo, wrapper);
                });
            },
            remove: function(e) {

                clearMidiPlayer();

                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },
            clear: function(e) {

                clearMidiPlayer();

                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },

            upload: function(e) {
                newFileName=_uuid();
                e.data = { newName: newFileName };
            },

            error: function(e) {
                clearMidiPlayer();
                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },
            complete: function(e) {
                console.log("Complete");
            },

            progress: function(e) {
                //kendoConsole.log("Upload progress :: " + e.percentComplete + "% :: " + getFileInfo(e));
            },

            success: function(e) {
                if(e.operation==='upload') {
                    var filename=newFileName;
                    var midiFilename = newFileName + ".midi"

                    console.log("Success:" + midiFilename);
                    var fileUrl3 = "/file/midi?file=" + midiFilename;
                    startMidiPlayer(fileUrl3);
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
                wrapper.find(".k-file[data-uid='" + file.uid + "'] .k-file-group-wrapper")
                    .replaceWith(preview);
                $("#img1").attr("src", this.result);
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
