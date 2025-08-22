import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { AuthProvider } from './contexts/AuthContext';
import Navbar from './components/common/Navbar';
import Footer from './components/common/Footer';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import SearchHotels from './pages/SearchHotels';
import HotelDetails from './pages/HotelDetails';
import MyReservations from './pages/MyReservations';
import Payment from './pages/Payment';
import AdminDashboard from './pages/admin/AdminDashboard';
import AdminReservations from './pages/admin/AdminReservations';
import ManageHotels from './pages/admin/ManageHotels';
import AddEditHotel from './pages/admin/AddEditHotel';
import ProtectedRoute from './components/common/ProtectedRoute';
import AdminRoute from './components/common/AdminRoute';

function App() {
  return (
    <AuthProvider>
      <div className="App">
        <Navbar />
        <main>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/search" element={<SearchHotels />} />
            <Route path="/hotel/:id" element={<HotelDetails />} />
            
            {/* Protected Routes */}
            <Route path="/reservations" element={
              <ProtectedRoute>
                <MyReservations />
              </ProtectedRoute>
            } />
            <Route path="/payment/:reservationId" element={
              <ProtectedRoute>
                <Payment />
              </ProtectedRoute>
            } />
            
            {/* Admin Routes */}
            <Route path="/admin" element={
              <AdminRoute>
                <AdminDashboard />
              </AdminRoute>
            } />
            <Route path="/admin/reservations" element={
              <AdminRoute>
                <AdminReservations />
              </AdminRoute>
            } />
            <Route path="/admin/hotels" element={
              <AdminRoute>
                <ManageHotels />
              </AdminRoute>
            } />
            <Route path="/admin/hotels/add" element={
              <AdminRoute>
                <AddEditHotel />
              </AdminRoute>
            } />
            <Route path="/admin/hotels/edit/:id" element={
              <AdminRoute>
                <AddEditHotel />
              </AdminRoute>
            } />
          </Routes>
        </main>
        <Footer />
        <ToastContainer
          position="top-right"
          autoClose={5000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
        />
      </div>
    </AuthProvider>
  );
}

export default App;
