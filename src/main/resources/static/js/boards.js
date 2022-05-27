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

var render = () => {
    let view = '';

    tags.forEach(element => {
        view += `<li type="button" class="list-inline-item" onclick="clickDeleteSearch('${element}')">#${element} <i class="bi bi-x-circle"></i></li>`;
    });

    $('#resultDiv2').replaceWith(`<ul class="list-inline" id="resultDiv2">${view}</ul>`);
}

var clearTag = () => {
    tags = [];
}

var setInputValue = (value) => {
    document.getElementById('exampleDataList').value = value;
}

var getInputValue = () => {
    return document.getElementById('exampleDataList').value;
}

var clickAddSearch = () => {
    addTag(getInputValue());
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

const exampleModal = document.getElementById('exampleModal')
exampleModal.addEventListener('show.bs.modal', event => {
    // Button that triggered the modal
    const button = event.relatedTarget
    // Extract info from data-bs-* attributes
    const recipient = button.getAttribute('data-bs-whatever')
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    const modalTitle = exampleModal.querySelector('.modal-title')
    const modalBodyInput = exampleModal.querySelector('.modal-body input')

    // modalTitle.textContent = `New message to ${recipient}`
    modalBodyInput.value = recipient
})