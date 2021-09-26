$(document).ready(function () {
    loadPlace();
    setInterval(function () {
        revalidateReserve();
    }, 60000);
});
