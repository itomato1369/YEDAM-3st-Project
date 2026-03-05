document.addEventListener("DOMContentLoaded", function () {
  /*	bootstrap-datepicker 플러그인을 적용한다
				jQuery만 알아듣는다 해서 씀
				class="datepicker"를 가진 input요소를 찾아서
				datepicker() method를 실행하겠다
				한국어 캘린더 사용을 위해 가져옴
			 */
  const projectStart = document.getElementById("projectStartDate").value;
  const projectEnd = document.getElementById("projectEndDate").value;

  $(".datepicker").datepicker({
    format: "yyyy-mm-dd",
    language: "ko",
    autoclose: true,
    startDate: projectStart,
    endDate: projectEnd,
  });
  // 근무한 날짜 선택시
  $("#workDate").on("changeDate", function (event) {
    // 근무일 선택 : 프로젝트 기간
    $("#workDate").datepicker("setStartDate", event.date);
    $("#workDate").datepicker("setEndDate", event.date);
  });

  document
    .getElementById("confirmSubmit")
    .addEventListener("click", function () {
      document.getElementById("workUpdate").submit();
    });
});
