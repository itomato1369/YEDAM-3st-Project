// 일감 상태 선택 모달
const issueModal = document.getElementById("issueSelectModal");
const jobInput = document.getElementById("jobTitle");

issueModal.querySelectorAll(".job-item").forEach((item) => {
  item.addEventListener("click", function () {
    jobInput.value = this.textContent.trim();

    // 이미 열려있는 modal 인스턴스를 가져와서 닫는
    // Bootstrap 공식 방식
    const modal = bootstrap.Modal.getInstance(issueModal);
    modal.hide();
  });
});
