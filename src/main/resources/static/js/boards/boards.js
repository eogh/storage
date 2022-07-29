var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

var tags = [];
var whatever = '';

var _last = false; // 페이지 마지막 여부
var _page = 0; // 페이지 번호
var _loading = false; // 로딩 여부

const exampleModal = document.getElementById('exampleModal');
const exampleModalInput = document.getElementById('exampleModalInput');
const input = document.getElementById('exampleDataList');

window.addEventListener('load', (e) => {
    findBoards(0);
});

window.addEventListener('scroll', () => {
    const SCROLLED_HEIGHT = window.scrollY;
    const WINDOW_HEIGHT = window.innerHeight;
    const OFFSET = 1;
    const HEADER_HEIGHT = 64;
    const MAIN_HEIGHT = document.querySelector('main').offsetHeight;
    const FOOTER_HEIGHT = 72;
    const IS_BOTTOM = WINDOW_HEIGHT + SCROLLED_HEIGHT + OFFSET > HEADER_HEIGHT + MAIN_HEIGHT + FOOTER_HEIGHT;

    if (IS_BOTTOM && _last === false && _loading === false) {
        console.log(`IS_BOTTOM`);
        findBoards(_page);
    }
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

var findBoards = (page) => {
    console.log(`findBoards page : ${page}`);
    _page = page;

    let boardSearchCond = {
        tags: tags
    }

    _loading = true;

    $.ajax({
        type: 'GET',
        url: `/boards/api/list?page=${page}`,
        // contentType: 'application/json; charset=UTF-8',
        // dataType: 'json',
        data: boardSearchCond,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(header, token);
        },
        success: function (data) {
            render(data.content);
            renderSearchTags();

            _last = data.last;
            _page++;
            _loading = false;
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}

var render = (boards) => {
    let view = '';

    boards.forEach(board => {
        let thumbnailPath = '/img/test01.jpg'; // default image
        if (board.boardFiles.length > 0) {
            thumbnailPath = board.boardFiles[0].file.thumbnailPath;
        }

        let tagView = '';
        board.boardTags.forEach(tag => {
            tagView += `<li type="button" class="list-inline-item" onclick="clickAddSearch('${tag.tag.name}')">${tag.tag.name}</li>`;
        });

        view += `<div class="col">
                    <div class="card h-100">
                        <a class="d-flex align-items-center h-100 p-1" href="/boards/${board.id}">
                            <img src="${thumbnailPath}" class="card-img" alt="...">
                        </a>
                        <div class="card-body">
                            <h5 class="card-title d-flex justify-content-between align-items-center"><span class="text-truncate">${board.title}</span></h5>
                            <ul class="list-inline">
                                ${tagView}
                                <li type="button" class="list-inline-item" data-bs-toggle="modal" data-bs-target="#exampleModal" data-bs-whatever="${board.id}">
                                    <i class="bi bi-plus-circle"></i>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>`;
    });
    
    if (_page === 0) $('#render').empty();
    $('#render').append(view);
}

var addTag = (tag) => {
    if (tag != '' && !tags.includes(tag)) {
        tags.push(tag);
        findBoards(0);
    }
}

var deleteTag = (tag) => {
    let index = tags.indexOf(tag);

    if (index >= 0) {
        tags.splice(index, 1);
        findBoards(0);
    }
}

var clearTag = () => {
    tags = [];
}

var renderSearchTags = () => {
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
            findBoards(0);
        },
        error: function (error) {
            console.error(`Error: ${error}`);
        }
    });
}