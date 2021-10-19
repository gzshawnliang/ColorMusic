<%--
  Created by IntelliJ IDEA.
  User: Shawn
  Date: 2021/10/18
  Time: 23:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>

    <link href="/styles/kendo.common.min.css" rel="stylesheet" type="text/css" />
    <link href="/styles/kendo.material.min.css" rel="stylesheet" type="text/css" />
    <script src="/js/jquery.min.js"></script>
    <script src="/js/kendo.web.min.js"></script>

</head>
<body>
测试上传图片 <span>${name}!</span>

<div>
    <input id="photo" name="photo"  type="file" />
</div>
<br>
<div>
    <img id="img1">
</div>

<script type="text/javascript">
    $(document).ready(function() {
        $("#photo").kendoUpload({
            validation: {
                allowedExtensions: [".jpg", ".png"],
                maxFileSize: 1024000,
                minFileSize: 10240
            },
            multiple: false,
            async: {
                saveUrl: "/file/upload",
                removeUrl: "/file/remove",
                autoUpload: false
            },
            select: function(e) {
                var fileInfo = e.files[0];
                var wrapper = this.wrapper;

                setTimeout(function () {
                    addPreview(fileInfo, wrapper);
                });
            },
            remove: function(e) {

                setTimeout(function () {
                    $("#img1").attr("src", "");
                });
            },
            clear: function(e) {

                    setTimeout(function () {
                        $("#img1").attr("src", "");
                    });
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
