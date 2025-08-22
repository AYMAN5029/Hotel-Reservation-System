import React from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';
import { FaSearch, FaHotel, FaCalendarAlt, FaCreditCard } from 'react-icons/fa';

const Home = () => {
  return (
    <div>
      {/* Hero Section */}
      <section className="hero-section">
        <Container>
          <Row>
            <Col>
              <h1 className="display-4 mb-4">Find Your Perfect Stay</h1>
              <p className="lead mb-4">
                Discover and book amazing hotels worldwide with our comprehensive reservation system
              </p>
              <LinkContainer to="/search">
                <Button variant="light" size="lg">
                  <FaSearch className="me-2" />
                  Search Hotels
                </Button>
              </LinkContainer>
            </Col>
          </Row>
        </Container>
      </section>

      {/* Features Section */}
      <Container className="my-5">
        <Row>
          <Col>
            <h2 className="text-center mb-5">Why Choose Our Hotel Reservation System?</h2>
          </Col>
        </Row>
        <Row>
          <Col md={3} className="mb-4">
            <Card className="h-100 text-center">
              <Card.Body>
                <FaSearch size={50} className="text-primary mb-3" />
                <Card.Title>Easy Search</Card.Title>
                <Card.Text>
                  Find hotels by city, state, country, or name with advanced filtering options
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-4">
            <Card className="h-100 text-center">
              <Card.Body>
                <FaHotel size={50} className="text-primary mb-3" />
                <Card.Title>Wide Selection</Card.Title>
                <Card.Text>
                  Choose from thousands of hotels worldwide with detailed information and reviews
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-4">
            <Card className="h-100 text-center">
              <Card.Body>
                <FaCalendarAlt size={50} className="text-primary mb-3" />
                <Card.Title>Flexible Booking</Card.Title>
                <Card.Text>
                  Book, modify, or cancel your reservations easily with our user-friendly interface
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={3} className="mb-4">
            <Card className="h-100 text-center">
              <Card.Body>
                <FaCreditCard size={50} className="text-primary mb-3" />
                <Card.Title>Secure Payment</Card.Title>
                <Card.Text>
                  Safe and secure payment processing with multiple payment options available
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>

      {/* Call to Action Section */}
      <section className="bg-light py-5">
        <Container>
          <Row>
            <Col className="text-center">
              <h3>Ready to Book Your Next Stay?</h3>
              <p className="lead mb-4">
                Join thousands of satisfied customers who trust our hotel reservation system
              </p>
              <LinkContainer to="/register">
                <Button variant="primary" size="lg" className="me-3">
                  Register Now
                </Button>
              </LinkContainer>
              <LinkContainer to="/search">
                <Button variant="outline-primary" size="lg">
                  Browse Hotels
                </Button>
              </LinkContainer>
            </Col>
          </Row>
        </Container>
      </section>
    </div>
  );
};

export default Home;
