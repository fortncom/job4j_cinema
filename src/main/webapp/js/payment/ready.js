$(document).ready(function () {
    $.ajax({
        type: 'GET',
        url: '/cinema/payment.do',
        dataType: 'json'
    }).done(function (data) {
        let result = "<div class=\"row pt-3\" id=\"comment\"><h3>"
        result += "Вы выбрали ряд " + data.row + " место " + data.cell
            + ", Сумма : " + data.sessionId.price + " рублей.";
        result += "</h3></div>";
        $("#comment").html(result);
        console.log(data);
    }).fail(function (err) {
        console.log(err);
    });
});