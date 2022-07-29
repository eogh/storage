var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var _tags = [];
var _files = [];

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');
const titleInput = document.getElementById('staticTitle');
const fileInput = document.querySelector('#file-input');

window.addEventListener('load', (e) => {
    $files.forEach(element => {
        _files.push(element.file);
    });
    renderFiles();

    $tags.forEach(element => {
        _tags.push(element.tag);
    });
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
                _files.push(result);
            });
            renderFiles();
            fileInput.value = null;
        },
        error: function (e) {
            console.log(e);
        }
    });
});

var clickDeleteFile = (fileId) => {
    
    if (fileId) {
        _files = _files.filter(e => e.id !== fileId);
        renderFiles();
    }
}

var renderFiles = () => {
    let view = '';

    view += `<label for="file-input" class="d-flex justify-content-center align-items-center" style="flex: 0 0 auto; width: 12rem; height: 9rem; border: 1px solid var(--bs-border-color); border-radius: 0.25em;">
                <div class="d-flex flex-column justify-content-center align-items-center">
                    <i class="bi bi-camera" style="color: gray; font-size: 2em"></i>
                    <div>${_files.length}/1000</div>
                </div>
            </label>`;

    _files.forEach(element => {
        view += `<img src="${element.thumbnailPath}" class="img-thumbnail ms-3 me-1" style="flex: 0 0 auto; width: 12rem; height: 9rem" alt="...">
                <i class="bi bi-x-circle text-muted" onclick="clickDeleteFile(${element.id})"></i>`
    });

    $('#render-files').replaceWith(`<div class="d-flex my-3 py-2" id="render-files" style="flex-wrap: nowrap; overflow-x: auto">${view}</div>`);
}

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

var clickEditBoard = () => {

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