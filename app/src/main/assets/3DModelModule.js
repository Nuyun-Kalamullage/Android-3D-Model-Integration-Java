const modelViewer = document.getElementById('3DModelViewer');
modelViewer.setAttribute("autoplay", false);

// Flag to track whether playAnimationOnce is executing
let isPlayingAnimationOnce = false;
let isLoopPlaying = false;
var loopAnimationName = 'Idle';

const speakButton = document.createElement('button');
speakButton.textContent = 'Talking';
speakButton.style.position = 'absolute';
speakButton.style.top = '10px';
speakButton.style.left = '10px';
speakButton.style.zIndex = '1';
speakButton.addEventListener('click', function () {
     playAnimationOnce('Yes');
     loopAnimationName = 'Yes';
});
document.body.appendChild(speakButton);

const speakButton1 = document.createElement('button');
speakButton1.textContent = 'Stop Talking';
speakButton1.style.position = 'absolute';
speakButton1.style.top = '50px';
speakButton1.style.left = '10px';
speakButton1.style.zIndex = '1';
speakButton1.addEventListener('click', function () {
     playAnimationOnce('ThumbsUp');
     loopAnimationName = 'Idle';
});
document.body.appendChild(speakButton1);

const play = () => {
    modelViewer.timeScale = 0.8;
    modelViewer.play({ repetitions: 1 });
};
// Call play when the model is loaded
modelViewer.addEventListener('load', play);

// Add event listeners for play, loop, and finished events
modelViewer.addEventListener('play', handlePlay);
modelViewer.addEventListener('finished', handleFinished);

// Function to change the animation name
async function playAnimationOnce(newAnimationName) {
    // Set the flag to true
    isPlayingAnimationOnce = true;
    // Deactivate the autoplay attribute
    modelViewer.setAttribute("autoplay", false);
    modelViewer.setAttribute('animation-name', newAnimationName);
    await modelViewer.updateComplete;
    if (newAnimationName == 'Yes') {
        modelViewer.timeScale = 1.5;
        modelViewer.play({ repetitions: 1 });
    } else if (newAnimationName == 'ThumbsUp') {
        modelViewer.timeScale = 1.5;
        modelViewer.play({ repetitions: 1 });
    }
}


// Function to change the animation name
async function playAnimationLoop(newAnimationName) {
    // Set the flag to true
    isPlayingAnimationOnce = true;
    // Deactivate the autoplay attribute
    modelViewer.setAttribute("autoplay", true);
    modelViewer.setAttribute('animation-name', newAnimationName);
    await modelViewer.updateComplete;

    if (newAnimationName == 'Yes') {
        modelViewer.timeScale = 1;
        modelViewer.play();
    }
}

// Function to stop the "Idle" animation loop
function stopIdleAnimationLoop() {
    // Add logic here to stop the "Idle" animation if needed
    // Example: modelViewer.pause();
}

// Event handler for the "play" event
function handlePlay() {
    console.log('Animation started playing.');
    // If playAnimationOnce is not executing, play the "Idle" animation
    if (!isPlayingAnimationOnce) {
        modelViewer.timeScale = 0.2;
        isLoopPlaying = false;
    if(loopAnimationName === 'Yes'){
        modelViewer.timeScale = 1.5;
        isLoopPlaying = true;
    }
        // Add logic here to play the "Idle" animation
        // Example:
        modelViewer.setAttribute('animation-name', loopAnimationName);
        // Example:
//        modelViewer.play({ repetitions: 1 });
    }
}

// Event handler for the "finished" event
function handleFinished() {
    console.log('Animation finished playing.');
    // Reset the flag when playAnimationOnce finishes
    isPlayingAnimationOnce = false;
    modelViewer.play();
}

function javascriptFunction(message) {
    playAnimationOnce(message);
}
