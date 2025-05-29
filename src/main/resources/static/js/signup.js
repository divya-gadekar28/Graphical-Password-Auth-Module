const container = document.getElementById('container');
const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');

let emojiList = [];      // will hold all fetched emojis
let fixedEmojiSet = [];  // the fixed 12 emojis from fetched list

let uppass = [], inpass = [];

// Constants for key derivation and encryption
const fixedPassword = "fixedSecretKey123!";
const fixedSalt = "uniqueSaltValue";

const storyEmojiGroups = {
  "🌅 Morning Routine": ["😴", "⏰", "🛁", "🪥", "☕", "👕"],
  "🧑‍💻 Work Time": ["💻", "🗂️", "📞", "✍️", "🧠", "📊"],
  "🍽️ Food Break": ["🍎", "🍕", "🍔", "🥤", "🍪", "🍩"],
  "🏃 Hobbies & Play": ["⚽", "🎮", "🎨", "🎧", "📚", "🧘"],
  "🌙 Night Wind-down": ["🛏️", "🌙", "📺", "🧴", "🪟", "😌"]
};

function generateStoryEmojiGrid(containerId, isSignup) {
  const container = document.getElementById(containerId);
  container.innerHTML = '';

  const previewId = isSignup ? 'signup-preview' : 'signin-preview';
  const updatePreview = () => {
    const selected = container.querySelectorAll('.emoji-button.selected');
    const selectedEmojis = Array.from(selected).map(btn => btn.textContent);
    document.getElementById(previewId).textContent = selectedEmojis.join(' ');
    if (isSignup) {
      uppass = selectedEmojis;
    } else {
      inpass = selectedEmojis;
    }
  };

  for (const [scene, emojis] of Object.entries(storyEmojiGroups)) {
    const section = document.createElement('div');
    section.className = 'emoji-story-section';

    const title = document.createElement('div');
    title.className = 'emoji-story-title';
    title.textContent = scene;
    section.appendChild(title);

    const row = document.createElement('div');
    row.className = 'emoji-row';

    emojis.forEach(emoji => {
      const button = document.createElement('button');
      button.type = 'button';
      button.textContent = emoji;
      button.className = 'emoji-button';

      button.onclick = () => {
        button.classList.toggle('selected');
        updatePreview();
      };

      row.appendChild(button);
    });

    section.appendChild(row);
    container.appendChild(section);
  }

  updatePreview(); // initial call
}


// Load story-based emojis directly, no fetch needed
function loadEmojis() {
  generateStoryEmojiGrid('signup-icons', true);
  generateStoryEmojiGrid('signin-icons', false);
}


// Hash emoji password + salt, returns base64 string
async function hashEmojiPassword(emojiString, salt) {
  const encoder = new TextEncoder();
  const data = encoder.encode(emojiString + salt);
  const hashBuffer = await crypto.subtle.digest('SHA-256', data);
  const bytes = new Uint8Array(hashBuffer);
  let binary = '';
  for (let b of bytes) binary += String.fromCharCode(b);
  return btoa(binary);
}

// In signup, send raw emoji password only:
async function signup(event) {
  event.preventDefault();

  const email = document.getElementById("upmail").value.trim();
  const name = document.getElementById("name").value.trim();

  if (!email || !name || uppass.length < 2) {
    alert("Please enter name, email and select at least two emojis.");
    return;
  }

  const rawPassword = uppass.join('');
  const encryptedIcons = await encryptIconPassword(rawPassword, fixedPassword, fixedSalt);

  try {
    const res = await fetch("http://localhost:8080/api/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        name,
        email,
        iconPassword: rawPassword,
        encryptedIconCodes: encryptedIcons
      })
    });

    if (!res.ok) {
      const errorText = await res.text();
      alert("Signup failed: " + errorText);
      return;
    }

    const data = await res.json();
    alert(`Signup successful for ${data.email}`);

    // Reset form and emojis
    document.getElementById("upmail").value = "";
    document.getElementById("name").value = "";
    uppass = [];
    generateEmojis('signup-icons', true);
  } catch (error) {
    console.error("Signup error:", error);
  }
}



// In signin, send raw emoji password only:
async function signin(event) {
  event.preventDefault();
  const email = document.getElementById("inmail").value.trim();
  if (!email || inpass.length < 2) {
    alert("Please enter email and select your emoji password.");
    return;
  }
  const emojiPassword = inpass.join('');
  const params = new URLSearchParams();
  params.append("email", email);
  params.append("iconPassword", emojiPassword);
  const res = await fetch("http://localhost:8080/api/login", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: params.toString()
  });
  const result = await res.text();
  alert(result);
  document.getElementById("inmail").value = "";
  inpass = [];
  generateEmojis('signin-icons', false);
}


signUpButton.addEventListener('click', () => {
  container.classList.add('right-panel-active');
  document.getElementById("upmail").value = "";
  document.getElementById("name").value = "";
  uppass = [];
  generateEmojis('signup-icons', true);
});

signInButton.addEventListener('click', () => {
  container.classList.remove('right-panel-active');
  document.getElementById("inmail").value = "";
  inpass = [];
  generateEmojis('signin-icons', false);
});

window.onload = () => {
  loadEmojis();
};


signUpButton.addEventListener('click', () => {
  container.classList.add('right-panel-active');

  // Reset fields and state
  document.getElementById("upmail").value = "";
  document.getElementById("name").value = "";
  uppass = [];

  // Reset preview and emojis
  document.getElementById('signup-preview').textContent = "";
  generateStoryEmojiGrid('signup-icons', true);
});

signInButton.addEventListener('click', () => {
  container.classList.remove('right-panel-active');

  // Reset fields and state
  document.getElementById("inmail").value = "";
  inpass = [];

  // Reset preview and emojis
  document.getElementById('signin-preview').textContent = "";
  generateStoryEmojiGrid('signin-icons', false);
});

// --- Encryption helpers ---

async function getKeyFromPassword(password, salt) {
  const encoder = new TextEncoder();
  const keyMaterial = await crypto.subtle.importKey(
    "raw",
    encoder.encode(password),
    { name: "PBKDF2" },
    false,
    ["deriveKey"]
  );

  return crypto.subtle.deriveKey(
    {
      name: "PBKDF2",
      salt: encoder.encode(salt),
      iterations: 100000,
      hash: "SHA-256",
    },
    keyMaterial,
    { name: "AES-GCM", length: 256 },
    false,
    ["encrypt", "decrypt"]
  );
}

async function encryptIconPassword(iconPassword, password, salt) {
  const key = await getKeyFromPassword(password, salt);
  const encoder = new TextEncoder();
  const iv = crypto.getRandomValues(new Uint8Array(12)); // 12 bytes IV

  const encrypted = await crypto.subtle.encrypt(
    {
      name: "AES-GCM",
      iv: iv,
    },
    key,
    encoder.encode(iconPassword)
  );

  // Combine IV + ciphertext
  const combined = new Uint8Array(iv.byteLength + encrypted.byteLength);
  combined.set(iv, 0);
  combined.set(new Uint8Array(encrypted), iv.byteLength);

  // Return base64 encoded string
  return btoa(String.fromCharCode(...combined));
}
