document.addEventListener("DOMContentLoaded", function () {
  const updateForm = document.getElementById("issueUpdate");
  const confirmUpdateButton = document.getElementById("updateSubmit");

  // 모달 내 '확인' 버튼 클릭 시
  if (confirmUpdateButton) {
    confirmUpdateButton.addEventListener("click", function () {
      
      //  수정 사유 체크 (필요하다면)
      const comment = updateForm.querySelector("input[name='comment']").value;
      if (!comment || comment.trim() === "") {
        alert("수정 사유를 입력해주세요.");
        return;
      }

      updateForm.submit();
    });
  }
  /* 첨부파일 관련 
               변수명 fileInput에 id가 files 가져옴
               변수명 fileList에 id가 fileList 가져옴
               변수 fileInput에 이벤트를 추가 
                change는 사용자가 input의 값을 변경하였을 때 발생하는 이벤트
               */
     const fileInput = document.getElementById("files");
     const fileList = document.getElementById("fileList");

     fileInput.addEventListener("change", function() {
         /* 선택된 다수의 파일 목록 생성 ul태그 밑에
         document.createElement로 li라는 변수를 만들고 거기에 li태그를 생성해서
         변수명 fileInput의 값 files의 길이 만큼 반복한다
         class이름을 list-group-item...이라는 bootstrap에 저장된 클래스를 부여하겠다
         텍스트 이름은 fileInput의 값에 들어있는 files의 배열 인덱스 번호 i번째의 이름값을 표시
         fileList변수에 추가하는 값이 li
         appendChild는 부모에 자식값을 추가 ul의 부모에 li라는 자식값을 추가
         */
         for (let i = 0;i < fileInput.files.length;i++) {
             const fileName = fileInput.files[i].name;

             /* 중복된 파일 생기지 않도록
             fileList의 자식요소 즉 id가 fileList인 ul 값의 자식이니 li
             Array.from()은 javascript 배열로 변환
             some() 조건을 만족하는 요소가 있으면 true를 반환
             그러한 값을 변수 exists에 넣는다
             */
             const exists = Array.from(fileList.children).some(
                 (li) => li.dataset.name === fileName,
             );

             // 중복된 파일이 아니라면 추가를 한다
             if (!exists) {
                 // 변수명 li에 li태그 새로 생성한다
                 const li = document.createElement("li");
                 li.className =
                     "list-group-item d-flex justify-content-between align-items-center";
                 // 파일 이름을 data 속성에 저장
                 li.dataset.name = fileName;
                 li.textContent = fileName;
                 // 삭제 버튼 생성 하는 변수 xButton
                 const xButton = document.createElement("button");
                 xButton.type = "button";
                 xButton.className = "btn btn-sm btn-danger";
                 xButton.textContent = "X";

                 /* 실제로 파일 데이터 삭제
                  화면에서는 지워지더라도 실제 서버는 
                  선택한 첨부파일의 데이터를 다 들고 있음 */
                 xButton.addEventListener("click", function() {
                     // 화면에서 해당 파일 제거
                     li.remove();
                     /* DataTransfer 객체 생성 
                      HTML에서 전송되는 데이터를 보관하고 관리하는데 사용
                     */
                     const dataTransfer = new DataTransfer();
                     // 현재 input 태그에 들어있는 실제 파일들 배열로 변환
                     // filter걸어서 파일명이 동일하지 않으면 거른다
                     // 다시 남은 파일들을담는다
                     Array.from(fileInput.files)
                         .filter((file) => !(file.name === fileName))
                         .forEach((file) => {
                             dataTransfer.items.add(file);
                         });
                     // 새로운 목록 교체
                     fileInput.files = dataTransfer.files;
                 });
                 // 클릭시 li 태그 삭제되는 이벤트 추가
                 xButton.addEventListener("click", function() {
                     li.remove();
                 });
                 //li태그 안에 삭제버튼 자식요소로 부모에추가
                 li.appendChild(xButton);
                 // ul태그 안에 이 모든것들 자식요소로 추가
                 fileList.appendChild(li);
             }
         }
     });

});