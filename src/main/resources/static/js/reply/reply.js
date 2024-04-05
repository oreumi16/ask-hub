

// 게시글 삭제 기능
if (document.querySelector('.delete-button') != null) {
    document.querySelector('.delete-button').addEventListener('click', ()=>{
        let articleId = document.getElementById('articleId').value;
        let check = confirm("정말로 삭제하시겠습니까?");
        if (check == false)
            return;
        fetch("/article?id=" + articleId, {
            method: "delete"})
            .then((response)=>{
                if(response.status == 200) { //성공
                    let str = response.text().then((res)=>{
                        alert("글을 삭제하였습니다");
                        location.href = res;
                    })
                }
                else if(response.status == 500) { //로그인 안했을 경우
                    alert("로그인이 필요합니다");
                }
                else if(response.status == 400) { // 유효성 미통과
                    response.text().then((res)=>{
                        let list = JSON.parse(res);
                        console.log(list);
                        let error = '';
                        list['arrayList'].forEach((e)=>{
                            error += e['message'] + '\n';
                        });
                        alert(error);
                    });
                }
            })
    })
}

// 댓글 생성 기능
function submitReply(button) {
    const articleId = document.getElementById('articleId').value;
    const comment = document.getElementById('comment-input').value;

    fetch(`/reply`, {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            articleId: articleId,
            comments: comment
        }),
    }).then((response) => {
        // 유효성 검증 로직
        if(response.status===201){
            alert('등록 완료되었습니다');
            location.href = '/article?id='+articleId
            // '/article?id='+articleId 로 변경해야함
            // window.location.reload();
        }
        else if(response.status===400){
            alert("댓글 내용을 입력해주세요")
            // 댓글에 내용이 아무것도 안 들어갔을 경우 발생
        }
        else if(response.status === 200) {
            alert("로그인이 필요합니다")
            location.href = response.url
        }
    });
}



// 댓글 수정 기능 (댓글 디자인 변경)
function updateReply(button){

    button.closest('.comment-item-container').querySelector('.comment-edit-container').classList.toggle('hidden');
    let content = button.closest('.comment-item-container').querySelector('.comment-contents').innerHTML;
    button.closest('.comment-item-container').querySelector('.comment-item-body').firstElementChild.outerHTML
        = '<textarea class="comment-input" placeholder="댓글을 달아보세요" name="contents">'+content+'</textarea>';
    button.closest('.comment-item-container').querySelector('.comment-editor-bottom').classList.toggle('hidden');

}

// 댓글 수정 기능 (댓글 수정 완료시)
function updateReplyComplete(button){
    let comments = button.closest('.comment-item-container').querySelector('.comment-input').value;
    const articleId = document.getElementById('articleId').value;
    const replyId = button.closest('.comment-item-container').querySelector('.comment-id').value;
    fetch(`/reply`, {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            replyId: replyId,
            comments: comments
        }),
    }).then((response) => {
        // 유효성 검증 로직
        if(response.status===200){
            alert('수정 완료되었습니다');
            location.href = '/article?id='+articleId
        }
        else if(response.status === 500) { //로그인 안했을 경우
            alert("로그인이 필요합니다");
            location.href = '/user/login'
        }
        else if(response.status === 403){
            alert("댓글을 작성한 작성자만 수정할 수 있습니다.")
            location.href = '/article?id='+articleId
        }
    });
}

// 댓글 수정 기능 (댓글 수정 취소)
function updateReplyCancel(button){
    const articleId = document.getElementById('articleId').value;
    location.href = '/article?id='+articleId
}



// 댓글 삭제 기능
function deleteReply(button) {
    const articleId = document.getElementById('articleId').value;
    const replyId = button.closest('.comment-item-container').querySelector('.comment-id').value;
    fetch(`/reply/${replyId}`, {
        method: 'DELETE'
    }).then((response) => {
        // 유효성 검증 로직
        if(response.status === 200) { //성공
            alert('삭제 완료되었습니다');
            location.href = '/article?id='+articleId
            // '/article?id='+articleId 로 변경해야함
            // window.location.reload();
        }
        else if(response.status === 500) { //로그인 안했을 경우
            alert("로그인이 필요합니다");
        }
        else if(response.status === 403){
            alert("댓글을 작성한 작성자만 삭제할 수 있습니다.")
        }
    });
}

