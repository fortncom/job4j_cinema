function loadPlace() {
    $.ajax({
        cache: false,
        type: 'GET',
        url: '/cinema/index',
        dataType: 'json'
    }).done(function (data) {
        let body = "<tbody>";
        let result = "<table class=\"table table-bordered\" id=\"tablePlace\">";
        result += "<thead><tr>";
        result += "<th style=\"width: 120px;\">Ряд / Место</th>";
        let currentRow = 0;
        let maxCell = 0;
        for (let i = 0; i < data.length; i++) {
            let place = data[i];
            if (place.row !== currentRow) {
                currentRow++;
            }
            body += "<tr><th>" + place.row + "</th>"
            let currentCell = i;
            while (place.row === currentRow) {
                if (place.cell > maxCell) {
                    result += "<th>" + place.cell + "</th>";
                    maxCell++;
                }
                let row = place.row;
                let cell = place.cell;
                body += "<td><input type=\"radio\" id=\"" + row + cell + "\" ";
                if (place.isReserved) {
                    body += "disabled ";
                }
                body += "name=\"place\" value=\"" + row + "," + cell + "\"> Ряд"
                    + row + ", Место" + cell + "</td>"
                if (++currentCell === data.length) {
                    break;
                }
                place = data[currentCell];
            }
            body += "<tr>"
            i = currentCell - 1;
        }
        result += "</tr></thead>";
        body += "</tbody>";
        body += "</table>";
        result += body;
        $("#tablePlace").html(result);
        console.log(data);
    }).fail(function (err) {
        console.log(err);
    });
};