//$(document).ready(function () {
//  var deleteButtons = document.querySelectorAll(".delete-btn");
//
//  deleteButtons.forEach(function (button) {
//    button.addEventListener("click", function () {
//      var id = button.getAttribute("data-id");
//      deleteData(id);
//    });
//  });
//});

$(document).ready(function () {
  $(document).on("click", ".delete-btn", function () {
    var id = $(this).attr("data-id");
    deleteData(id);
  });
});

function deleteData(id) {
    $.ajax({
        url: "/"+ id,
        method: "DELETE",
        success: function() {
            console.log("Data with ID " + id + " successfully deleted.");
            // 웹 페이지에서 해당 행을 제거합니다.
            $("button[data-id='" + id + "']").closest("tr").remove();
        },
        error: function(xhr, textStatus, errorThrown) {
            console.error("Error deleting data with ID " + id + ": " + textStatus + " - " + errorThrown);
        }
    });
}
