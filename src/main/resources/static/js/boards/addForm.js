var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var tags = [];
var files = [];
// var whatever = '';

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');
const titleInput = document.getElementById('staticTitle');

// TODO: 태그, 파일 삭제기능

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

var clickAddFile = () => {
    console.log(`clickAddFile`);

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
        success: function (file) {
            let view = `<img src="${file.path}" class="img-thumbnail m-3" style="width: 200px; height: 200px" alt="...">`;
            $('#staticFiles').append(view);

            files.push(file);
            // TODO: input창 초기화
        },
        error: function (e) {
            console.log(e);
        }
    });
}

var clickAddTag = () => {
    // const boardId = whatever;
    const inputValue = exampleModalInput.value;

    let boardTagForm = {
        tagName: inputValue
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
            let view = `<li class="list-inline-item">#${tag.name}</li>`;
            $('#staticTags').before(view);

            tags.push(tag);
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}

var clickAddBoard = () => {

    const inputValue = titleInput.value;

    let boardSaveForm = {
        title: inputValue,
        tags: tags,
        files: files
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

