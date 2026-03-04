import { openModal } from '../components/modals.js';
import { API_BASE_URL } from '../config/config.js';

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

window.onload = function () {
  const adminBtn = document.getElementById('adminLogin') || document.getElementById('admin-role');
  if (adminBtn) {
    adminBtn.addEventListener('click', () => {
      openModal('adminLogin');
    });
  }

  const doctorBtn = document.getElementById('doctorLogin') || document.getElementById('doctor-role');
  if (doctorBtn) {
    doctorBtn.addEventListener('click', () => {
      openModal('doctorLogin');
    });
  }
};

window.adminLoginHandler = async function adminLoginHandler() {
  const username = document.getElementById('username')?.value?.trim();
  const password = document.getElementById('password')?.value;
  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(admin)
    });

    if (!response.ok) {
      alert('Invalid credentials!');
      return;
    }

    const data = await response.json();
    localStorage.setItem('token', data.token);
    selectRole('admin');
  } catch (error) {
    console.error('Admin login failed:', error);
    alert('Something went wrong. Please try again.');
  }
};

window.doctorLoginHandler = async function doctorLoginHandler() {
  const email = document.getElementById('email')?.value?.trim();
  const password = document.getElementById('password')?.value;
  const doctor = { identifier:email, password };
  console.log('Doctor login attempt:', doctor);
  try {
    const response = await fetch(DOCTOR_API, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(doctor)
    });
    console.log('Doctor login response:', response);
    if (!response.ok) {
      alert('Invalid credentials!');
      return;
    }

    const data = await response.json();
    localStorage.setItem('token', data.token);
    selectRole('doctor');
  } catch (error) {
    console.error('Doctor login failed:', error);
    alert('Something went wrong. Please try again.');
  }
};
