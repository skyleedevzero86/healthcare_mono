
function getUserList(searchUserId){
    var chartList = [];
    $.ajax({
        type:"POST",
        url:"/userInfo/drguardianList",
        dataType:"JSON",
        data:{ "userId": searchUserId},
        success : function(data) {
         populateTable(data);
        },
        complete : function(data) {
        },
        error : function(xhr, status, error) {
              console.log(error);
        }
    });
}

function populateTable(data) {
        var tableBody = $('#userTable');

        tableBody.empty();

        data.forEach(function(item) {
            ci
        });

    }