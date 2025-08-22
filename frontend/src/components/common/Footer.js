import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import { FaHotel, FaPhone, FaEnvelope, FaMapMarkerAlt } from 'react-icons/fa';

const Footer = () => {
  return (
    <footer className="footer">
      <Container>
        <Row>
          <Col md={6}>
            <h5>
              <FaHotel className="me-2" />
              Hotel Reservation System
            </h5>
            <p>Your trusted partner for finding and booking the perfect hotel for your stay.</p>
          </Col>
          <Col md={6}>
            <h5>Quick Links</h5>
            <ul className="list-unstyled">
              <li><a href="/" className="text-light">Home</a></li>
              <li><a href="/search" className="text-light">Search Hotels</a></li>
              <li><a href="/login" className="text-light">Login</a></li>
              <li><a href="/register" className="text-light">Register</a></li>
            </ul>
          </Col>
        </Row>
        <hr className="my-4" />
        <Row>
          <Col className="text-center">
            <p>&copy; 2025 Hotel Reservation System. All rights reserved.</p>
          </Col>
        </Row>
      </Container>
    </footer>
  );
};

export default Footer;
