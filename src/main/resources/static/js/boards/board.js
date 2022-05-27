var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var whatever = '';

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');

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
            location.reload();
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}