function revalidateReserve() {
    $.ajax({
        cache: false,
        type: 'GET',
        url: '/cinema/index',
        dataType: 'json'
    }).done(function (data) {
        for (let i = 0; i < data.length; i++) {
            let id = "" + data[i].row + data[i].cell;
            if (data[i].isReserved) {
                document.getElementById(id).disabled=true;
            } else {
                document.getElementById(id).disabled=false;
            }
        }
        alert(data)
    }).fail(function (err) {
        console.log(err);
    });
};