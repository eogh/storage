var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var _tags = [];
var _files = [];

// var whatever = '';

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');
const titleInput = document.getElementById('staticTitle');
const fileInput = document.querySelector('#file-input');
const preview = document.querySelector('#preview');

window.addEventListener('load', (e) => {
    renderTags();
});

exampleModal.addEventListener('show.bs.modal', event => {
    // Button that triggered the modal
    const button = event.relatedTarget
    // Extract info from data-bs-* attributes
    // whatever = button.getAttribute('data-bs-whatever')
    // If necessary, you could initiate an AJAX request here
    // and then do the updating in a callback.
    //
    // Update the modal's content.
    exampleModalInput.value = '';
});

fileInput.addEventListener('change', () => {
    const files = Array.from(fileInput.files);
    // files.forEach(file => {
    //     console.log(`filename : ${file.name}`);
    // });

    $.ajax({
        type: 'POST',
        url: `/files/add`,
        data: new FormData($("#upload-file-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (results) {
            results.forEach(result => {
                preview.innerHTML += `<img src="${result.path}" class="img-thumbnail m-3" style="width: 10rem; height: 10rem" alt="...">`;
        
                _files.push(result);
                fileInput.value = null;
            });
        },
        error: function (e) {
            console.log(e);
        }
    });
});

var renderTags = () => {
    let view = '';

    _tags.forEach(element => {
        view += `<li class="list-inline-item mt-1">
                    <button type="button" class="btn btn-light" onclick="clickDeleteTag(${element.id})">
                        ${element.name} <i class="bi bi-x-circle text-muted"></i>
                    </button>
                </li>`;
    });

    view += `<li class="list-inline-item mt-1">
                <button type="button" class="btn btn-link" data-bs-toggle="modal" data-bs-target="#exampleModal"><i class="bi bi-plus-circle text-muted"></i></button>
            </li>`;

    $('#render-tags').replaceWith(`<ul class="list-inline" id="render-tags">${view}</ul>`);
}

var clickAddTag = () => {
    const tagName = exampleModalInput.value;

    if (tagName != '' && _tags.filter(e => e.name === tagName).length == 0) {

        let boardTagForm = {
            tagName: tagName
        }

        $.ajax({
            type: 'POST',
            url: `/tags/api/add`,
            contentType: 'application/json',
            // dataType: 'json',
            data: JSON.stringify(boardTagForm),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (tag) {
                _tags.push(tag);
                renderTags();
            },
            error: function (error) {
                console.error(`Error: ${error}`);
            }
        });
    }
}

var clickDeleteTag = (tagId) => {

    if (tagId) {
        _tags = _tags.filter(e => e.id !== tagId);
        renderTags();
    }
}

var clickAddBoard = () => {

    let boardSaveForm = {
        title: titleInput.value,
        tags: _tags,
        files: _files
    }

    $.ajax({
        type: 'POST',
        url: `/boards/api/add`,
        contentType: 'application/json',
        // dataType: 'json',
        data: JSON.stringify(boardSaveForm),
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (board) {
            location.href = `/boards/${board.id}`;
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}

