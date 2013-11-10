require([
  '$api/models',

], function(models, languageExample, coverExample, buttonExample, playlistExample) {
  'use strict';
    models.player.load('track').done(updateCurrentTrack);
    models.player.addEventListener('change', updateCurrentTrack);

    function updateCurrentTrack() {
        console.log(models.player.track)
    }
});
