import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Container, NavDropdown } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { useAuth } from '../../contexts/AuthContext';
import { FaHotel, FaUser, FaSignOutAlt, FaCog } from 'react-icons/fa';

const Navbar = () => {
  const { user, logout, isAuthenticated, isAdmin } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <BootstrapNavbar bg="dark" variant="dark" expand="lg" sticky="top">
      <Container>
        <LinkContainer to="/">
          <BootstrapNavbar.Brand>
            <FaHotel className="me-2" />
            Hotel Reservation System
          </BootstrapNavbar.Brand>
        </LinkContainer>
        
        <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
        <BootstrapNavbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <LinkContainer to="/">
              <Nav.Link>Home</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/search">
              <Nav.Link>Search Hotels</Nav.Link>
            </LinkContainer>
            {isAuthenticated() && (
              <LinkContainer to="/reservations">
                <Nav.Link>My Reservations</Nav.Link>
              </LinkContainer>
            )}
            {isAdmin() && (
              <NavDropdown title="Admin" id="admin-dropdown">
                <LinkContainer to="/admin">
                  <NavDropdown.Item>Dashboard</NavDropdown.Item>
                </LinkContainer>
                <LinkContainer to="/admin/hotels">
                  <NavDropdown.Item>Manage Hotels</NavDropdown.Item>
                </LinkContainer>
              </NavDropdown>
            )}
          </Nav>
          
          <Nav>
            {isAuthenticated() ? (
              <NavDropdown title={
                <span>
                  <FaUser className="me-1" />
                  {user?.username}
                </span>
              } id="user-dropdown">
                <NavDropdown.Item onClick={handleLogout}>
                  <FaSignOutAlt className="me-2" />
                  Logout
                </NavDropdown.Item>
              </NavDropdown>
            ) : (
              <>
                <LinkContainer to="/login">
                  <Nav.Link>Login</Nav.Link>
                </LinkContainer>
                <LinkContainer to="/register">
                  <Nav.Link>Register</Nav.Link>
                </LinkContainer>
              </>
            )}
          </Nav>
        </BootstrapNavbar.Collapse>
      </Container>
    </BootstrapNavbar>
  );
};

export default Navbar;
