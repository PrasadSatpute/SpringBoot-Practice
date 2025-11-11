function updateProgress(slider, progressTextId) {
    const value = slider.value;
    document.getElementById(progressTextId).innerText = value + '%';
  }

  function submitProgressUpdates() {
      const sliders = $("input[type='range']");

      const token = $('meta[name="_csrf"]').attr('content');
      const header = $('meta[name="_csrf_header"]').attr('content');

      sliders.each(function() {
          const slider = $(this);
          const taskId = slider.attr('id').split('-')[1];
          const completionPercentage = Math.round(parseFloat(slider.val()));

//          const completionPercentage = slider.val();

          $.ajax({
              url: `/tasks/updateCompletion/${taskId}`,
              type: 'POST',
              contentType: 'application/x-www-form-urlencoded',
              data: $.param({ completionPercentage: completionPercentage }),
              beforeSend: function(xhr) {
                  if (token && header) {
                      xhr.setRequestHeader(header, token);
                  }
              },
              success: function(data) {
                  if (data.error) {
                      alert(data.error); // or show error somewhere on the page
                  } else if (data.redirectUrl) {
                      window.location.href = data.redirectUrl;
                  }
              },
              error: function(jqXHR, textStatus, errorThrown) {
                  console.error("Error updating task progress:", errorThrown);
              }
          });
      });
  }

