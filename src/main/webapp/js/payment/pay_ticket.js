function payTicket() {
    if (validate() === true) {
        let name = $("#username").val();
        let email = $("#email").val();
        let phone = $("#phone").val();
        let arr = {name: name, email: email, phone: phone};
        $.ajax({
            async: false,
            url: '/cinema/payment.do',
            type: 'POST',
            data: JSON.stringify(arr),
            dataType: 'json',
            contentType: 'application/json'
        }).done(function (data) {
            if (data !== "409") {
                paid(data);
            } else {
                failPaid(data);
            }
        }).fail(function (err) {
            alert("Error: " + err);
        });
    }
}

function paid(data) {
    let result = "<div class=\"row pt-3\" id=\"comment\">"
        + "<h4>" + "Билет на " + data.row + " ряд " + data.cell + " место успешно куплен." + "</h4></div>";
    $("#container").html(result);
}

function failPaid() {
    let result = "<div class=\"row pt-3\" id=\"comment\">"
        + "<h4>" + "Произошла ошибка при оплате, билет не был куплен." + "</h4></div>" +
        "<div class=\"row float-right\" id=\"buttonId\">" +
        "        <button type=\"button\" class=\"btn btn-success\" onclick=\"redirect()\">Возврат к выбору места</button>" +
        "    </div>";
    $("#container").html(result);
}

function redirect() {
    window.location.href="http://localhost:8080/cinema/welcome/index.html";
}

