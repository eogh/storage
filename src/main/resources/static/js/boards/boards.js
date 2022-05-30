var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var tags = [];
var whatever = '';

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');
const input = document.getElementById('exampleDataList');

window.addEventListener('load', (e) => {
    findBoards();
});

exampleModal.addEventListener('show.bs.modal', event => {
    // Button that triggered the modal
    const button = event.relatedTarget
    // Extract info from data-bs-* attributes
    whatever = button.getAttribute('data-bs-whatever')
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    exampleModalInput.value = '';
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
            render();
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}

var addTag = (tag) => {
    if (tag != '' && !tags.includes(tag)) {
        tags.push(tag);
        findBoards();
    }
}

var deleteTag = (tag) => {
    let index = tags.indexOf(tag);

    if (index >= 0) {
        tags.splice(index, 1);
        findBoards();
    }
}

var clearTag = () => {
    tags = [];
}

var render = () => {
    let view = '';

    tags.forEach(element => {
        view += `<li type="button" class="list-inline-item" onclick="clickDeleteSearch('${element}')">#${element} <i class="bi bi-x-circle"></i></li>`;
    });

    $('#resultDiv2').replaceWith(`<ul class="list-inline" id="resultDiv2">${view}</ul>`);
}


var setInputValue = (value) => {
    input.value = value;
}

var getInputValue = () => {
    return input.value;
}

var clickAddSearch = (tag) => {
    addTag(tag||getInputValue());
    setInputValue('');
}

var clickSearch = (tag) => {
    clearTag();
    addTag(tag||getInputValue());
    setInputValue('');
}

var clickDeleteSearch = (tag) => {
    deleteTag(tag);
}

var clickAddTag = () => {
    const boardId = whatever;
    const inputValue = exampleModalInput.value;

    let boardTagForm = {
        tagName: inputValue
    }
    
    $.ajax({
        type: 'POST',
        url: `/boards/api/${boardId}/tags/add`,
        contentType: 'application/json',
        // dataType: 'json',
        data: JSON.stringify(boardTagForm),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            console.log(data);
            findBoards();
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}