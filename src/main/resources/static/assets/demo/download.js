$(document).ready(function () {
    $("#upload-file-button").click(function () {
        let fileName = $("#file-name-input").val();
        $.post("/api/files/upload/" + fileName, function () {
            alert("File uploaded successfully");
            location.reload();
        }).fail(function () {
            alert("Failed to upload file");
        });
    });
});