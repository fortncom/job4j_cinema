function validate() {
    const answer = "Please fill: ";
    let result = answer;
    if ($('#username').val() === '') {
        result += "\n - field Name ";
    }
    if ($('#phone').val() === '') {
        result += "\n - field Email ";
    }
    if ($('#email').val() === '') {
        result += "\n - field Phone ";
    }
    if (answer !== result) {
        alert(result);
        return false;
    }
    return true;
}