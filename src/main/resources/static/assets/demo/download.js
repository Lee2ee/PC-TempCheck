document.getElementById("exportDataBtn").addEventListener("click", function () {
    fetch("/exportCurrentData", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        }
    }).then(function (response) {
        if (response.ok) {
            alert("엑셀파일이 저장되었습니다.");
        } else {
            alert("Error occurred while exporting data.");
        }
    });
});
