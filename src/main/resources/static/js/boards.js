var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var tags = [];

window.addEventListener('load', (e) => {
    findBoards();
});

var findBoards = () => {
    console.log(`findBoards : ${tags}`);

    let boardSearchCond = {
        tags: tags
    }

    $.ajax({
        type: 'GET',
        url: `/boards/api/list`,
        // contentType: 'application/json; charset=UTF-8',
        // dataType: 'json',
        data: boardSearchCond,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (fragment) {
            $('#resultDiv').replaceWith(fragment);
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}

var addTag = (tag) => {
    if (!tags.includes(tag)) {
        tags.push(tag);
    }
}

var deleteTag = (tag) => {
    let index = tags.indexOf(tag);

    if (index >= 0) {
        tags.splice(index, 1);
    }
}

var clearTag = () => {
    tags = [];
}

var addSearch = () => {
    let value = document.getElementById('exampleDataList').value;

    addTag(value);
    findBoards();
}

var search = () => {
    let value = document.getElementById('exampleDataList').value;

    clearTag();
    addTag(value);
    findBoards();
}

var deleteSearch = (tag) => {
    deleteTag(tag);
    findBoards();
}