import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button, Form, Modal, Badge, Spinner } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { hotelAPI, reservationAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-toastify';
import { FaMapMarkerAlt, FaStar, FaRupeeSign, FaCalendarAlt, FaUsers } from 'react-icons/fa';

const HotelDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  const [hotel, setHotel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showBookingModal, setShowBookingModal] = useState(false);
  const [bookingData, setBookingData] = useState({
    checkInDate: '',
    checkOutDate: '',
    numberOfGuests: 1,
    numberOfRooms: 1,
    roomType: 'AC',
    specialRequests: ''
  });
  const [bookingLoading, setBookingLoading] = useState(false);

  useEffect(() => {
    loadHotelDetails();
  }, [id]);

  const loadHotelDetails = async () => {
    try {
      const response = await hotelAPI.getHotelById(id);
      setHotel(response.data);
    } catch (error) {
      toast.error('Failed to load hotel details');
      console.error('Error loading hotel:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleBookingChange = (e) => {
    setBookingData({
      ...bookingData,
      [e.target.name]: e.target.value
    });
  };

  const handleBooking = async (e) => {
    e.preventDefault();
    
    if (!isAuthenticated()) {
      toast.info('Please login to make a reservation');
      navigate('/login');
      return;
    }

    setBookingLoading(true);
    
    try {
      const reservationPayload = {
        hotelId: parseInt(id),
        userId: user.userId,
        checkInDate: bookingData.checkInDate,
        checkOutDate: bookingData.checkOutDate,
        numberOfGuests: parseInt(bookingData.numberOfGuests),
        numberOfRooms: parseInt(bookingData.numberOfRooms),
        roomType: bookingData.roomType,
        specialRequests: bookingData.specialRequests,
        totalCost: calculateTotalCost()
      };

      const response = await reservationAPI.createReservation(reservationPayload);
      toast.success('Reservation created successfully!');
      setShowBookingModal(false);
      
      // Navigate to payment page
      navigate(`/payment/${response.data.reservationId}`);
    } catch (error) {
      toast.error('Failed to create reservation');
      console.error('Booking error:', error);
    } finally {
      setBookingLoading(false);
    }
  };

  const calculateTotalCost = () => {
    if (!hotel || !bookingData.checkInDate || !bookingData.checkOutDate) return 0;
    
    const checkIn = new Date(bookingData.checkInDate);
    const checkOut = new Date(bookingData.checkOutDate);
    const nights = Math.ceil((checkOut - checkIn) / (1000 * 60 * 60 * 24));
    
    const roomCost = bookingData.roomType === 'AC' ? hotel.acRoomCost : hotel.nonAcRoomCost;
    return nights * roomCost * bookingData.numberOfRooms;
  };

  const getTodayDate = () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      </Container>
    );
  }

  if (!hotel) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <h3>Hotel not found</h3>
            <Button variant="primary" onClick={() => navigate('/search')}>
              Back to Search
            </Button>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <Button variant="outline-secondary" onClick={() => navigate('/search')} className="mb-3">
            ← Back to Search
          </Button>
        </Col>
      </Row>

      <Row>
        <Col lg={8}>
          <Card>
            <Card.Body>
              <div className="d-flex justify-content-between align-items-start mb-3">
                <div>
                  <h2>{hotel.hotelName}</h2>
                  <p className="text-muted mb-2">
                    <FaMapMarkerAlt className="me-1" />
                    {hotel.address}, {hotel.city}, {hotel.state}, {hotel.country}
                  </p>
                  <Badge bg="success">
                    <FaStar className="me-1" />
                    {hotel.avgRatingByCustomers || 'N/A'} Rating
                  </Badge>
                </div>
              </div>

              <hr />

              <h5>About This Hotel</h5>
              <p>{hotel.description}</p>

              <hr />

              <Row>
                <Col md={6}>
                  <h6>Room Rates & Availability</h6>
                  <div className="mb-2">
                    <FaRupeeSign className="me-1 text-success" />
                    <strong>AC Room:</strong> ₹{hotel.acRoomCost} per night
                    <br />
                    <small className="text-muted">
                      Available: {hotel.availableAcRooms || 0} / {hotel.totalAcRooms || 0} rooms
                    </small>
                  </div>
                  <div className="mb-2">
                    <FaRupeeSign className="me-1 text-success" />
                    <strong>Non-AC Room:</strong> ₹{hotel.nonAcRoomCost} per night
                    <br />
                    <small className="text-muted">
                      Available: {hotel.availableNonAcRooms || 0} / {hotel.totalNonAcRooms || 0} rooms
                    </small>
                  </div>
                </Col>
                <Col md={6}>
                  <h6>Hotel Information</h6>
                  <div className="mb-2">
                    <strong>City:</strong> {hotel.city}
                  </div>
                  <div className="mb-2">
                    <strong>State:</strong> {hotel.state}
                  </div>
                  <div className="mb-2">
                    <strong>Country:</strong> {hotel.country}
                  </div>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="sticky-top" style={{ top: '20px' }}>
            <Card.Body>
              <h5 className="mb-3">Book This Hotel</h5>
              <div className="mb-3">
                <div className="d-flex justify-content-between mb-2">
                  <span>AC Room:</span>
                  <strong>₹{hotel.acRoomCost}/night</strong>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Non-AC Room:</span>
                  <strong>₹{hotel.nonAcRoomCost}/night</strong>
                </div>
              </div>
              <Button 
                variant="primary" 
                size="lg" 
                className="w-100"
                onClick={() => setShowBookingModal(true)}
              >
                <FaCalendarAlt className="me-2" />
                Book Now
              </Button>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Booking Modal */}
      <Modal show={showBookingModal} onHide={() => setShowBookingModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Book {hotel.hotelName}</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleBooking}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Check-in Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="checkInDate"
                    value={bookingData.checkInDate}
                    onChange={handleBookingChange}
                    min={getTodayDate()}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Check-out Date</Form.Label>
                  <Form.Control
                    type="date"
                    name="checkOutDate"
                    value={bookingData.checkOutDate}
                    onChange={handleBookingChange}
                    min={bookingData.checkInDate || getTodayDate()}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>

            <Row>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    <FaUsers className="me-1" />
                    Number of Guests
                  </Form.Label>
                  <Form.Control
                    type="number"
                    name="numberOfGuests"
                    value={bookingData.numberOfGuests}
                    onChange={handleBookingChange}
                    min="1"
                    max="10"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Number of Rooms</Form.Label>
                  <Form.Control
                    type="number"
                    name="numberOfRooms"
                    value={bookingData.numberOfRooms}
                    onChange={handleBookingChange}
                    min="1"
                    max="5"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Room Type</Form.Label>
                  <Form.Select
                    name="roomType"
                    value={bookingData.roomType}
                    onChange={handleBookingChange}
                    required
                  >
                    <option value="AC">AC Room (₹{hotel.acRoomCost}/night)</option>
                    <option value="NON_AC">Non-AC Room (₹{hotel.nonAcRoomCost}/night)</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Special Requests (Optional)</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="specialRequests"
                value={bookingData.specialRequests}
                onChange={handleBookingChange}
                placeholder="Any special requests or preferences..."
              />
            </Form.Group>

            {bookingData.checkInDate && bookingData.checkOutDate && (
              <div className="bg-light p-3 rounded">
                <h6>Booking Summary</h6>
                <div className="d-flex justify-content-between">
                  <span>Total Cost:</span>
                  <strong>₹{calculateTotalCost()}</strong>
                </div>
              </div>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowBookingModal(false)}>
              Cancel
            </Button>
            <Button 
              variant="primary" 
              type="submit" 
              disabled={bookingLoading || !bookingData.checkInDate || !bookingData.checkOutDate}
            >
              {bookingLoading ? 'Processing...' : 'Confirm Booking'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default HotelDetails;
